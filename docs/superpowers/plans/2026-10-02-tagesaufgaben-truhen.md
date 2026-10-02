# Tagesaufgaben und Schatztruhen Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Drei Tagesaufgaben auf dem Lernpfad und Schatztruhen, aus denen Bonus-XP oder gesperrte Accessoires und T-Shirts kommen.

**Architecture:** Die Aufgaben und der Truhen-Inhalt sind reine Logik ohne Android (`DailyQuests`, `ChestLogic`) und werden per Unit-Test geprüft. Gerechnet wird nur mit Werten, die im `UserProgress` stehen; neu sind vier Tageszähler sowie die Mengen `openedChests` und `unlockedItems`. Die Oberfläche liest den Fortschritt wie bisher über `collectAsState`.

**Tech Stack:** Kotlin 2.0.21, Jetpack Compose (BOM 2024.12.01), DataStore Preferences, JUnit 4. Keine neuen Abhängigkeiten. Spec: `docs/superpowers/specs/2026-10-02-tagesaufgaben-truhen-design.md`.

## Global Constraints

- Keine neuen Bibliotheken; Truhe und Effekte werden gezeichnet.
- Tageszähler werden beim Datumswechsel zurückgesetzt, genau wie `xpToday`.
- Truhe nach jeder dritten Lektion (`CHEST_EVERY = 3`), zusätzlich eine Tages-Truhe.
- Truhen-Inhalt: 5–20 XP oder ein gesperrtes Teil; alles frei → immer XP.
- Jede Truhe öffnet genau einmal.
- Deutsche KDoc-Kommentare, Test-Namen deutsch in Backticks.
- Gebaut und geprüft wird über GitHub Actions (`claude/**`), danach `adb install -r`.

## Dateien

| Datei | Aktion |
|---|---|
| `app/src/main/java/de/turkischlernen/app/data/content/DailyQuests.kt` | neu |
| `app/src/test/java/de/turkischlernen/app/DailyQuestsTest.kt` | neu |
| `app/src/main/java/de/turkischlernen/app/data/progress/ChestLogic.kt` | neu |
| `app/src/test/java/de/turkischlernen/app/ChestLogicTest.kt` | neu |
| `app/src/main/java/de/turkischlernen/app/data/progress/UserProgress.kt` | ändern |
| `app/src/main/java/de/turkischlernen/app/data/progress/ProgressRepository.kt` | ändern |
| `app/src/main/java/de/turkischlernen/app/data/settings/AvatarConfig.kt` | ändern |
| `app/src/main/java/de/turkischlernen/app/ui/path/DailyQuestsCard.kt` | neu |
| `app/src/main/java/de/turkischlernen/app/ui/rewards/ChestOverlay.kt` | neu |
| `app/src/main/java/de/turkischlernen/app/ui/path/PathScreen.kt` | ändern |
| `app/src/main/java/de/turkischlernen/app/ui/profile/ProfileScreen.kt` | ändern |
| `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonViewModel.kt` | ändern |

---

### Task 1: Tagesaufgaben (TDD)

**Files:** Create `data/content/DailyQuests.kt`, Test `test/.../DailyQuestsTest.kt`

**Interfaces – Produces:**
- `enum class QuestKind { LESSONS, XP, PERFECT, WORDS, CORRECT }`
- `data class Quest(val id: String, val kind: QuestKind, val goal: Int, val label: String, val emoji: String)`
- `DailyQuests.pool: List<Quest>`
- `DailyQuests.forDay(date: LocalDate): List<Quest>` – genau drei verschiedene, aus dem Datum gewürfelt
- `DailyQuests.progressOf(quest: Quest, progress: UserProgress): Int`
- `DailyQuests.isDone(quest: Quest, progress: UserProgress): Boolean`
- `DailyQuests.allDone(date: LocalDate, progress: UserProgress): Boolean`

- [ ] **Step 1: Failing Test schreiben** (`DailyQuestsTest.kt`)

```kotlin
package de.turkischlernen.app

import de.turkischlernen.app.data.content.DailyQuests
import de.turkischlernen.app.data.progress.UserProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DailyQuestsTest {

    private val tag: LocalDate = LocalDate.of(2026, 10, 2)

    @Test
    fun `derselbe Tag ergibt immer dieselben drei Aufgaben`() {
        val a = DailyQuests.forDay(tag).map { it.id }
        val b = DailyQuests.forDay(tag).map { it.id }
        assertEquals(a, b)
        assertEquals(3, a.size)
    }

    @Test
    fun `die drei Aufgaben sind verschieden`() {
        val ids = DailyQuests.forDay(tag).map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `jede Aufgabe hat Text, Emoji und ein Ziel groesser null`() {
        DailyQuests.pool.forEach { quest ->
            assertTrue(quest.label.isNotBlank())
            assertTrue(quest.emoji.isNotBlank())
            assertTrue(quest.goal > 0)
        }
    }

    @Test
    fun `der Fortschritt wird aus den Tageszaehlern gelesen`() {
        val progress = UserProgress(
            xpToday = 12,
            lessonsToday = 1,
            perfectToday = 2,
            wordsToday = 7,
            correctToday = 9
        )
        DailyQuests.pool.forEach { quest ->
            val wert = DailyQuests.progressOf(quest, progress)
            assertTrue("negativer Fortschritt bei ${quest.id}", wert >= 0)
        }
    }

    @Test
    fun `eine Aufgabe gilt ab dem Ziel als erledigt`() {
        val quest = DailyQuests.pool.first { it.kind == de.turkischlernen.app.data.content.QuestKind.XP }
        assertFalse(DailyQuests.isDone(quest, UserProgress(xpToday = quest.goal - 1)))
        assertTrue(DailyQuests.isDone(quest, UserProgress(xpToday = quest.goal)))
        assertTrue(DailyQuests.isDone(quest, UserProgress(xpToday = quest.goal + 50)))
    }
}
```

- [ ] **Step 2: Commit + CI prüfen – erwartet FAIL** (`Unresolved reference: DailyQuests`)

- [ ] **Step 3: Implementierung schreiben**

```kotlin
package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.progress.UserProgress
import java.time.LocalDate
import kotlin.random.Random

/** Worauf sich eine Tagesaufgabe bezieht. */
enum class QuestKind { LESSONS, XP, PERFECT, WORDS, CORRECT }

/** Eine Tagesaufgabe mit Ziel, Text und Symbol. */
data class Quest(
    val id: String,
    val kind: QuestKind,
    val goal: Int,
    val label: String,
    val emoji: String
)

/** Die drei Aufgaben des Tages – immer gleich für denselben Tag. */
object DailyQuests {

    const val COUNT = 3

    val pool = listOf(
        Quest("lektion1", QuestKind.LESSONS, 1, "Schaffe 1 Lektion", "✅"),
        Quest("lektion2", QuestKind.LESSONS, 2, "Schaffe 2 Lektionen", "🏁"),
        Quest("xp20", QuestKind.XP, 20, "Sammle 20 XP", "⭐"),
        Quest("xp40", QuestKind.XP, 40, "Sammle 40 XP", "🌟"),
        Quest("perfekt1", QuestKind.PERFECT, 1, "Beende eine Runde ohne Fehler", "🎯"),
        Quest("woerter10", QuestKind.WORDS, 10, "Übe 10 Wörter", "🔤"),
        Quest("richtig15", QuestKind.CORRECT, 15, "Antworte 15-mal richtig", "👍")
    )

    /** Drei verschiedene Aufgaben, aus dem Datum gewürfelt. */
    fun forDay(date: LocalDate): List<Quest> =
        pool.shuffled(Random(date.toEpochDay())).take(COUNT)

    fun progressOf(quest: Quest, progress: UserProgress): Int = when (quest.kind) {
        QuestKind.LESSONS -> progress.lessonsToday
        QuestKind.XP -> progress.xpToday
        QuestKind.PERFECT -> progress.perfectToday
        QuestKind.WORDS -> progress.wordsToday
        QuestKind.CORRECT -> progress.correctToday
    }

    fun isDone(quest: Quest, progress: UserProgress): Boolean =
        progressOf(quest, progress) >= quest.goal

    /** Alle drei Aufgaben des Tages erledigt? Dann gibt es die Tages-Truhe. */
    fun allDone(date: LocalDate, progress: UserProgress): Boolean =
        forDay(date).all { isDone(it, progress) }
}
```

- [ ] **Step 4: Commit + CI prüfen – erwartet PASS**

---

### Task 2: Truhen-Logik (TDD)

**Files:** Create `data/progress/ChestLogic.kt`, Test `test/.../ChestLogicTest.kt`

**Interfaces – Produces:**
- `sealed interface ChestReward { data class Xp(val amount: Int) : ChestReward; data class Item(val id: String, val label: String) : ChestReward }`
- `ChestLogic.CHEST_EVERY = 3`, `MIN_XP = 5`, `MAX_XP = 20`
- `ChestLogic.chestAfter(lessonIndex: Int): Boolean` – true nach jeder dritten Lektion
- `ChestLogic.chestId(lessonId: String): String`
- `ChestLogic.dailyChestId(date: LocalDate): String`
- `ChestLogic.roll(random: Random, unlocked: Set<String>): ChestReward`

- [ ] **Step 1: Failing Test schreiben**

```kotlin
package de.turkischlernen.app

import de.turkischlernen.app.data.progress.ChestLogic
import de.turkischlernen.app.data.progress.ChestReward
import de.turkischlernen.app.data.settings.AvatarOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class ChestLogicTest {

    @Test
    fun `nach jeder dritten Lektion steht eine Truhe`() {
        assertFalse(ChestLogic.chestAfter(1))
        assertFalse(ChestLogic.chestAfter(2))
        assertTrue(ChestLogic.chestAfter(3))
        assertTrue(ChestLogic.chestAfter(6))
    }

    @Test
    fun `Truhen-Ids sind stabil und unterscheidbar`() {
        assertEquals("chest_u_essen_l3", ChestLogic.chestId("u_essen_l3"))
        assertEquals("chest_tag_2026-10-02", ChestLogic.dailyChestId(LocalDate.of(2026, 10, 2)))
    }

    @Test
    fun `XP-Gewinne liegen im erlaubten Bereich`() {
        val alleTeile = AvatarOptions.lockedItemIds.toSet()
        repeat(200) { durchlauf ->
            val reward = ChestLogic.roll(Random(durchlauf), alleTeile)
            assertTrue(reward is ChestReward.Xp)
            val xp = (reward as ChestReward.Xp).amount
            assertTrue("XP zu klein: $xp", xp >= ChestLogic.MIN_XP)
            assertTrue("XP zu gross: $xp", xp <= ChestLogic.MAX_XP)
        }
    }

    @Test
    fun `ein bereits freigeschaltetes Teil kommt nie noch einmal`() {
        val schonFrei = AvatarOptions.lockedItemIds.drop(1).toSet()
        repeat(200) { durchlauf ->
            val reward = ChestLogic.roll(Random(durchlauf), schonFrei)
            if (reward is ChestReward.Item) {
                assertEquals(AvatarOptions.lockedItemIds.first(), reward.id)
            }
        }
    }

    @Test
    fun `ohne freigeschaltete Teile kommen auch Teile vor`() {
        val gefunden = (1..200).map { ChestLogic.roll(Random(it), emptySet()) }
        assertTrue(gefunden.any { it is ChestReward.Item })
        assertTrue(gefunden.any { it is ChestReward.Xp })
    }
}
```

- [ ] **Step 2: Commit + CI prüfen – erwartet FAIL**

- [ ] **Step 3: Implementierung schreiben**

```kotlin
package de.turkischlernen.app.data.progress

import de.turkischlernen.app.data.settings.AvatarOptions
import java.time.LocalDate
import kotlin.random.Random

/** Was in einer Truhe liegt. */
sealed interface ChestReward {
    data class Xp(val amount: Int) : ChestReward
    data class Item(val id: String, val label: String) : ChestReward
}

/** Wo Truhen stehen und was sie enthalten. */
object ChestLogic {

    const val CHEST_EVERY = 3
    const val MIN_XP = 5
    const val MAX_XP = 20

    /** Jede dritte Lektion wird von einer Truhe belohnt. */
    fun chestAfter(lessonNumber: Int): Boolean = lessonNumber > 0 && lessonNumber % CHEST_EVERY == 0

    fun chestId(lessonId: String): String = "chest_$lessonId"

    fun dailyChestId(date: LocalDate): String = "chest_tag_$date"

    /**
     * Würfelt den Inhalt: in etwa jedem dritten Fall ein noch gesperrtes Teil,
     * sonst XP. Sind alle Teile frei, gibt es immer XP.
     */
    fun roll(random: Random, unlocked: Set<String>): ChestReward {
        val offen = AvatarOptions.lockedItemIds.filterNot { it in unlocked }
        if (offen.isNotEmpty() && random.nextInt(3) == 0) {
            val id = offen[random.nextInt(offen.size)]
            return ChestReward.Item(id, AvatarOptions.itemLabel(id))
        }
        return ChestReward.Xp(MIN_XP + random.nextInt(MAX_XP - MIN_XP + 1))
    }
}
```

- [ ] **Step 4: Commit + CI prüfen – erwartet PASS**

---

### Task 3: Gesperrte Teile im Avatar

**Files:** Modify `data/settings/AvatarConfig.kt`

**Interfaces – Produces:**
- `AccessoryOption(..., val locked: Boolean = false)`, `ColorOption(..., val locked: Boolean = false)`
- `AvatarOptions.lockedItemIds: List<String>` – Ids aller freischaltbaren Teile
- `AvatarOptions.itemLabel(id: String): String`
- `AvatarOptions.isUnlocked(id: String, unlocked: Set<String>): Boolean`

- [ ] **Step 1: Felder und neue Teile ergänzen**

```kotlin
data class AccessoryOption(
    val id: String,
    val label: String,
    val emoji: String,
    val locked: Boolean = false
)

data class ColorOption(
    val id: String,
    val label: String,
    val hex: Long,
    val locked: Boolean = false
)
```

Neue Einträge in den Listen:

```kotlin
        AccessoryOption("krone", "Krone", "👑", locked = true),
        AccessoryOption("halstuch", "Halstuch", "🧶", locked = true),
        AccessoryOption("wintermuetze", "Wintermütze", "🎩", locked = true)
```

```kotlin
        ColorOption("regenbogen", "Regenbogen", 0xFF7C4DFF, locked = true),
        ColorOption("gold", "Gold", 0xFFFFC107, locked = true)
```

- [ ] **Step 2: Hilfsfunktionen ergänzen**

```kotlin
    /** Alle Teile, die man erst aus einer Truhe bekommt. */
    val lockedItemIds: List<String> =
        accessories.filter { it.locked }.map { it.id } + shirts.filter { it.locked }.map { it.id }

    fun itemLabel(id: String): String =
        accessories.firstOrNull { it.id == id }?.label
            ?: shirts.firstOrNull { it.id == id }?.label
            ?: id

    /** Freie Teile sind immer verfügbar, gesperrte erst nach dem Fund. */
    fun isUnlocked(id: String, unlocked: Set<String>): Boolean {
        val gesperrt = id in lockedItemIds
        return !gesperrt || id in unlocked
    }
```

- [ ] **Step 3: Zeichnung ergänzen** – in `ui/mascot/Kangal.kt` im `when (accessoryId)` die neuen Fälle:

```kotlin
                "krone" -> drawCrown()
                "halstuch" -> drawScarf()
                "wintermuetze" -> drawCap()
```

und eine Krone zeichnen:

```kotlin
/** Goldene Krone auf dem Kopf. */
private fun DrawScope.drawCrown() {
    val gold = Color(0xFFFFC107)
    val path = Path().apply {
        moveTo(68f, 52f)
        lineTo(76f, 30f)
        lineTo(88f, 46f)
        lineTo(100f, 26f)
        lineTo(112f, 46f)
        lineTo(124f, 30f)
        lineTo(132f, 52f)
        close()
    }
    drawPath(path, color = gold)
    drawPath(path, color = Line, style = Stroke(2.5f, join = StrokeJoin.Round))
    drawCircle(Color(0xFFE53935), radius = 3.5f, center = Offset(100f, 44f))
}
```

- [ ] **Step 4: Commit + CI prüfen – erwartet PASS**

---

### Task 4: Fortschritt zählt mit

**Files:** Modify `data/progress/UserProgress.kt`, `data/progress/ProgressRepository.kt`, `ui/lesson/LessonViewModel.kt`

**Interfaces – Produces:**
- `UserProgress(..., lessonsToday, perfectToday, wordsToday, correctToday, openedChests: Set<String>, unlockedItems: Set<String>)`
- `ProgressRepository.completeLesson(..., correctAnswers: Int)` – neuer Parameter
- `ProgressRepository.openChest(chestId: String, reward: ChestReward)`

- [ ] **Step 1: `UserProgress` erweitern**

```kotlin
    val lessonsToday: Int = 0,
    val perfectToday: Int = 0,
    val wordsToday: Int = 0,
    val correctToday: Int = 0,
    val openedChests: Set<String> = emptySet(),
    val unlockedItems: Set<String> = emptySet(),
```

- [ ] **Step 2: Schlüssel und Tages-Reset in `ProgressRepository`**

Neue Keys: `lessons_today`, `perfect_today`, `words_today`, `correct_today`,
`opened_chests` (StringSet), `unlocked_items` (StringSet).
Im `toProgress()` werden die vier Zähler wie `xpToday` beim Datumswechsel auf 0 gesetzt.

- [ ] **Step 3: `completeLesson` zählt mit**

```kotlin
            val heute = lastActive == today.toString()
            prefs[Keys.lessonsToday] = (if (heute) prefs[Keys.lessonsToday] ?: 0 else 0) + 1
            prefs[Keys.correctToday] = (if (heute) prefs[Keys.correctToday] ?: 0 else 0) + correctAnswers
            prefs[Keys.wordsToday] = (if (heute) prefs[Keys.wordsToday] ?: 0 else 0) + practicedItemIds.size
            if (mistakes == 0) {
                prefs[Keys.perfectToday] = (if (heute) prefs[Keys.perfectToday] ?: 0 else 0) + 1
            }
```

- [ ] **Step 4: `openChest` ergänzen**

```kotlin
    /** Truhe öffnen: XP gutschreiben bzw. Teil freischalten – nur einmal je Truhe. */
    suspend fun openChest(chestId: String, reward: ChestReward) {
        store.edit { prefs ->
            val offen = prefs[Keys.openedChests] ?: emptySet()
            if (chestId in offen) return@edit
            prefs[Keys.openedChests] = offen + chestId
            when (reward) {
                is ChestReward.Xp -> {
                    prefs[Keys.totalXp] = (prefs[Keys.totalXp] ?: 0) + reward.amount
                    prefs[Keys.xpToday] = (prefs[Keys.xpToday] ?: 0) + reward.amount
                }

                is ChestReward.Item ->
                    prefs[Keys.unlockedItems] = (prefs[Keys.unlockedItems] ?: emptySet()) + reward.id
            }
        }
    }
```

- [ ] **Step 5: `LessonViewModel.complete()` übergibt die richtigen Antworten**

```kotlin
            repository.completeLesson(
                lessonId = lesson?.id,
                earnedXp = earnedXp,
                mistakes = mistakes,
                practicedItemIds = lesson?.itemIds ?: practicedIds,
                correctAnswers = correctAnswers
            )
```

(`correctAnswers` dafür von `private var` auf `var … private set` ändern.)

- [ ] **Step 6: Commit + CI prüfen – erwartet PASS**

---

### Task 5: Truhe als Overlay

**Files:** Create `ui/rewards/ChestOverlay.kt`

**Interfaces – Produces:**
- `@Composable fun ChestOverlay(reward: ChestReward, onClose: () -> Unit)`

- [ ] **Step 1: Overlay bauen** – dunkler Hintergrund, gezeichnete Truhe (Kiste, Deckel klappt
      beim Öffnen um −32°, Lichtstrahlen dahinter), `ConfettiOverlay()` darüber, Text mit dem
      Gewinn („+12 XP" bzw. „Neu: Krone 👑"), Knopf „NEHMEN" ruft `onClose`.
      Der Kangal steht mit `MascotMood.CHEER` daneben.

- [ ] **Step 2: Commit + CI prüfen – erwartet PASS**

---

### Task 6: Lernpfad und Profil

**Files:** Modify `ui/path/PathScreen.kt`, Create `ui/path/DailyQuestsCard.kt`, Modify `ui/profile/ProfileScreen.kt`

- [ ] **Step 1: `DailyQuestsCard`** – Karte „Tagesaufgaben" mit drei Zeilen: Emoji, Text,
      `ThickProgressBar(fraction = fortschritt / ziel)`, rechts „3/10" bzw. ein grüner Haken.

- [ ] **Step 2: `PathScreen`** – Karte unter dem Tagesziel einsetzen; Tages-Truhe anzeigen,
      wenn `DailyQuests.allDone(...)` und die Truhe noch zu ist; nach jeder dritten Lektion
      einen Truhen-Knoten einfügen (`ChestLogic.chestAfter(position)`), gesperrt, solange die
      Lektion davor nicht abgeschlossen ist. Antippen öffnet `ChestOverlay`.

- [ ] **Step 3: `ProfileScreen`** – gesperrte Accessoires und Shirts mit 🔒 zeichnen und
      nicht antippbar machen (`AvatarOptions.isUnlocked(id, progress.unlockedItems)`).

- [ ] **Step 4: Commit + CI prüfen – erwartet PASS**

---

### Task 7: Aufs Handy und prüfen

- [ ] **Step 1:** APK laden und installieren (`ship.sh` erledigt Commit, CI, Download, Installation).
- [ ] **Step 2: Prüfliste**
  - Lernpfad zeigt oben drei Tagesaufgaben mit Fortschritt
  - Eine Lektion spielen → Fortschritt der Aufgaben wächst
  - Nach drei Lektionen erscheint ein Truhen-Knoten
  - Truhe antippen: Deckel springt auf, Konfetti, Gewinn wird angezeigt
  - Gewonnenes Teil taucht im Profil ohne 🔒 auf und lässt sich auswählen
  - Bereits geöffnete Truhe bleibt offen und gibt nichts mehr
