# Serien-Kalender und Level Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Serien-Kalender hinter der Flamme und ein Level aus den XP, mit gefeiertem Aufstieg samt Truhe und Extra-Spielzeit.

**Architecture:** Zwei neue reine Logik-Objekte (`LevelLogic`, `StreakCalendar`) mit Unit-Tests. Das Level wird aus `totalXp` berechnet und nicht gespeichert; neu gespeichert werden nur die aktiven Tage und die längste Serie. Die Oberfläche hängt sich in vorhandene Stellen ein: Kopfzeile, Abschluss-Ablauf, Profil.

**Tech Stack:** Kotlin 2.0.21, Jetpack Compose (BOM 2024.12.01), DataStore, JUnit 4. Keine neuen Abhängigkeiten. Spec: `docs/superpowers/specs/2026-10-02-serie-und-level-design.md`.

## Global Constraints

- 100 XP je Level, Level 1 ab 0 XP.
- Aufstieg gibt eine Truhe (`chest_level_<n>`) und 5 Minuten Spielzeit.
- Aktive Tage werden auf 90 Einträge begrenzt.
- Keine neuen Bibliotheken; deutsche KDoc, Test-Namen deutsch in Backticks.
- Gebaut und geprüft über GitHub Actions (`claude/**`), danach `adb install -r`.

## Dateien

| Datei | Aktion |
|---|---|
| `data/progress/LevelLogic.kt` | neu |
| `data/progress/StreakCalendar.kt` | neu |
| `test/.../LevelLogicTest.kt`, `test/.../StreakCalendarTest.kt` | neu |
| `data/progress/UserProgress.kt`, `ProgressRepository.kt` | ändern |
| `ui/stats/StreakOverlay.kt` | neu |
| `ui/components/StatsBar.kt`, `ui/navigation/AppRoot.kt` | ändern |
| `ui/lesson/LessonViewModel.kt`, `ui/lesson/LessonCompleteScreen.kt`, `ui/lesson/LessonScreen.kt` | ändern |
| `ui/profile/ProfileScreen.kt` | ändern |

---

### Task 1: Level-Logik (TDD)

**Produces:** `LevelLogic.XP_PER_LEVEL`, `LEVEL_UP_PLAY_MINUTES`, `levelOf(xp): Int`, `xpIntoLevel(xp): Int`, `xpToNext(xp): Int`, `leveledUp(before: Int, after: Int): Boolean`, `chestId(level: String)`

- [ ] **Step 1: Test schreiben** (`LevelLogicTest.kt`)

```kotlin
package de.turkischlernen.app

import de.turkischlernen.app.data.progress.LevelLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelLogicTest {

    @Test
    fun `hundert XP ergeben ein Level`() {
        assertEquals(1, LevelLogic.levelOf(0))
        assertEquals(1, LevelLogic.levelOf(99))
        assertEquals(2, LevelLogic.levelOf(100))
        assertEquals(3, LevelLogic.levelOf(250))
    }

    @Test
    fun `Rest-XP und fehlende XP passen zusammen`() {
        assertEquals(85, LevelLogic.xpIntoLevel(285))
        assertEquals(15, LevelLogic.xpToNext(285))
        assertEquals(LevelLogic.XP_PER_LEVEL, LevelLogic.xpIntoLevel(285) + LevelLogic.xpToNext(285))
    }

    @Test
    fun `ein Aufstieg wird erkannt`() {
        assertTrue(LevelLogic.leveledUp(before = 95, after = 110))
        assertFalse(LevelLogic.leveledUp(before = 101, after = 150))
        assertFalse(LevelLogic.leveledUp(before = 300, after = 300))
    }

    @Test
    fun `jede Levelstufe hat eine eigene Truhe`() {
        assertEquals("chest_level_4", LevelLogic.chestId(4))
        assertTrue(LevelLogic.chestId(5) != LevelLogic.chestId(6))
    }
}
```

- [ ] **Step 2: Commit + CI – erwartet FAIL**
- [ ] **Step 3: Implementierung**

```kotlin
package de.turkischlernen.app.data.progress

/** Aus XP wird ein Level: je [XP_PER_LEVEL] Punkte eine Stufe. */
object LevelLogic {

    const val XP_PER_LEVEL = 100

    /** Extra-Spielzeit beim Aufstieg. */
    const val LEVEL_UP_PLAY_MINUTES = 5

    fun levelOf(totalXp: Int): Int = totalXp.coerceAtLeast(0) / XP_PER_LEVEL + 1

    /** Bereits gesammelte XP innerhalb der aktuellen Stufe. */
    fun xpIntoLevel(totalXp: Int): Int = totalXp.coerceAtLeast(0) % XP_PER_LEVEL

    /** Noch fehlende XP bis zur nächsten Stufe. */
    fun xpToNext(totalXp: Int): Int = XP_PER_LEVEL - xpIntoLevel(totalXp)

    fun leveledUp(before: Int, after: Int): Boolean = levelOf(after) > levelOf(before)

    fun chestId(level: Int): String = "chest_level_$level"
}
```

- [ ] **Step 4: Commit + CI – erwartet PASS**

---

### Task 2: Serien-Kalender-Logik (TDD)

**Produces:** `StreakCalendar.MAX_HISTORY_DAYS = 90`, `weekOf(date): List<LocalDate>`, `isActive(date, activeDays: Set<String>): Boolean`, `longestStreak(activeDays: Set<String>): Int`, `trim(activeDays: Set<String>, today: LocalDate): Set<String>`

- [ ] **Step 1: Test schreiben** (`StreakCalendarTest.kt`)

```kotlin
package de.turkischlernen.app

import de.turkischlernen.app.data.progress.StreakCalendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class StreakCalendarTest {

    private val freitag: LocalDate = LocalDate.of(2026, 10, 2)

    @Test
    fun `die Woche geht von Montag bis Sonntag`() {
        val woche = StreakCalendar.weekOf(freitag)
        assertEquals(7, woche.size)
        assertEquals(DayOfWeek.MONDAY, woche.first().dayOfWeek)
        assertEquals(DayOfWeek.SUNDAY, woche.last().dayOfWeek)
        assertTrue(freitag in woche)
    }

    @Test
    fun `aktive Tage werden erkannt`() {
        val tage = setOf("2026-10-01", "2026-10-02")
        assertTrue(StreakCalendar.isActive(freitag, tage))
        assertFalse(StreakCalendar.isActive(freitag.plusDays(1), tage))
    }

    @Test
    fun `die laengste Serie zaehlt zusammenhaengende Tage`() {
        val tage = setOf(
            "2026-09-20", "2026-09-21", "2026-09-22",
            "2026-09-25",
            "2026-10-01", "2026-10-02"
        )
        assertEquals(3, StreakCalendar.longestStreak(tage))
    }

    @Test
    fun `ohne Tage gibt es keine Serie`() {
        assertEquals(0, StreakCalendar.longestStreak(emptySet()))
    }

    @Test
    fun `alte Tage werden abgeschnitten`() {
        val alt = freitag.minusDays(200).toString()
        val neu = freitag.toString()
        val getrimmt = StreakCalendar.trim(setOf(alt, neu), freitag)
        assertTrue(neu in getrimmt)
        assertFalse(alt in getrimmt)
    }
}
```

- [ ] **Step 2: Commit + CI – erwartet FAIL**
- [ ] **Step 3: Implementierung**

```kotlin
package de.turkischlernen.app.data.progress

import java.time.DayOfWeek
import java.time.LocalDate

/** Rechnet mit den Tagen, an denen gelernt wurde. */
object StreakCalendar {

    /** So viele Tage Verlauf werden gespeichert. */
    const val MAX_HISTORY_DAYS = 90

    /** Montag bis Sonntag der Woche, in der [date] liegt. */
    fun weekOf(date: LocalDate): List<LocalDate> {
        val montag = date.minusDays(((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong()))
        return (0L until 7L).map { montag.plusDays(it) }
    }

    fun isActive(date: LocalDate, activeDays: Set<String>): Boolean = date.toString() in activeDays

    /** Längste Kette aufeinanderfolgender Tage. */
    fun longestStreak(activeDays: Set<String>): Int {
        val tage = activeDays.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted()
        if (tage.isEmpty()) return 0

        var laengste = 1
        var aktuell = 1
        tage.zipWithNext { vorher, jetzt ->
            if (vorher.plusDays(1) == jetzt) aktuell++ else aktuell = 1
            if (aktuell > laengste) laengste = aktuell
        }
        return laengste
    }

    /** Wirft Tage weg, die älter als [MAX_HISTORY_DAYS] sind. */
    fun trim(activeDays: Set<String>, today: LocalDate): Set<String> {
        val grenze = today.minusDays(MAX_HISTORY_DAYS.toLong())
        return activeDays.filter { tag ->
            runCatching { LocalDate.parse(tag) >= grenze }.getOrDefault(false)
        }.toSet()
    }
}
```

- [ ] **Step 4: Commit + CI – erwartet PASS**

---

### Task 3: Fortschritt speichert die Tage

**Files:** `UserProgress.kt`, `ProgressRepository.kt`

- [ ] **Step 1: `UserProgress` erweitern**

```kotlin
    /** Tage, an denen gelernt wurde (ISO-Datum), und die längste je erreichte Serie. */
    val activeDays: Set<String> = emptySet(),
    val longestStreak: Int = 0,
```

- [ ] **Step 2: Schlüssel `active_days` (StringSet) und `longest_streak` (Int) ergänzen und im `toProgress()` lesen.**

- [ ] **Step 3: In `completeLesson` den Tag eintragen**

```kotlin
            val tage = StreakCalendar.trim(
                (prefs[Keys.activeDays] ?: emptySet()) + today.toString(),
                today
            )
            prefs[Keys.activeDays] = tage
            val serie = ProgressLogic.nextStreak(lastActive, streak, today)
            prefs[Keys.longestStreak] = maxOf(prefs[Keys.longestStreak] ?: 0, serie, StreakCalendar.longestStreak(tage))
```

- [ ] **Step 4: `addPlayMinutes` ergänzen**

```kotlin
    /** Zusätzliche Spielzeit, z. B. beim Level-Aufstieg. */
    suspend fun addPlayMinutes(minutes: Int) {
        store.edit { prefs -> prefs[Keys.playMinutes] = (prefs[Keys.playMinutes] ?: 0) + minutes }
    }
```

- [ ] **Step 5: Commit + CI – erwartet PASS**

---

### Task 4: Serien-Einblendung

**Files:** `ui/stats/StreakOverlay.kt` (neu), `ui/components/StatsBar.kt`, `ui/navigation/AppRoot.kt`

- [ ] **Step 1: `StreakOverlay`** – dunkler Hintergrund, Karte mit Überschrift „Deine Serie",
      sieben Kreisen (Kürzel Mo–So darüber), aktiver Tag orange mit 🔥, heute mit Rand,
      darunter „N Tage Serie" und „Längste Serie: M Tage", Knopf „SCHLIESSEN".

- [ ] **Step 2: `StatsBar`** – `onStreakClick: (() -> Unit)? = null` an den Flammen-Chip weitergeben.

- [ ] **Step 3: `AppRoot`** – `var showStreak by remember { mutableStateOf(false) }`; `StatsBar(progress, onStreakClick = { showStreak = true })`; bei `showStreak` das Overlay über dem Inhalt zeigen.

- [ ] **Step 4: Commit + CI – erwartet PASS**

---

### Task 5: Level-Aufstieg feiern

**Files:** `ui/lesson/LessonViewModel.kt`, `ui/lesson/LessonCompleteScreen.kt`, `ui/lesson/LessonScreen.kt`

- [ ] **Step 1: ViewModel** – in `complete()` nach dem Speichern:

```kotlin
            if (LevelLogic.leveledUp(before.totalXp, after.totalXp)) {
                newLevel = LevelLogic.levelOf(after.totalXp)
                repository.addPlayMinutes(LevelLogic.LEVEL_UP_PLAY_MINUTES)
            }
```

mit `var newLevel by mutableStateOf<Int?>(null) private set`.

- [ ] **Step 2: Abschluss-Bildschirm** – Parameter `newLevel: Int?`; in die Einblendungs-Liste
      nach Abzeichen und Serie `CelebrationOverlay.Level(level)` aufnehmen: große Zahl,
      „Level N erreicht!", „+5 Minuten PlayStation", Kangal jubelt.
      Danach `ChestOverlay` mit `ChestLogic.roll(Random(LevelLogic.chestId(level).hashCode()), progress.unlockedItems)`,
      sofern die Truhe noch nicht geöffnet ist; beim Schließen `openChest` aufrufen.

- [ ] **Step 3: `LessonScreen`** – `newLevel = viewModel.newLevel` durchreichen.

- [ ] **Step 4: Commit + CI – erwartet PASS**

---

### Task 6: Level im Profil

**Files:** `ui/profile/ProfileScreen.kt`

- [ ] **Step 1:** Unter dem Namen „Level N" anzeigen, darunter `ThickProgressBar` mit
      `LevelLogic.xpIntoLevel(progress.totalXp) / 100f` und der Text
      „Noch X XP bis Level N+1".

- [ ] **Step 2: Commit + CI – erwartet PASS**

---

### Task 7: Aufs Handy und prüfen

- [ ] **Step 1:** `ship.sh` ausführen (Commit, CI, Download, Installation).
- [ ] **Step 2: Prüfliste**
  - Flamme antippen öffnet den Wochen-Kalender, heutiger Tag markiert
  - Nach einer Lektion ist der heutige Tag orange
  - Profil zeigt Level und Rest-XP
  - Beim Überschreiten einer Hunderter-Marke: Level-Einblendung, danach Truhe, +5 Minuten
  - Dieselbe Level-Truhe gibt es kein zweites Mal
