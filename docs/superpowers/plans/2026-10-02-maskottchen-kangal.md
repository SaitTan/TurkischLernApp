# Maskottchen (Kangal) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ein im Code gezeichneter Kangal-Hund begleitet das Kind in Lektion, Abschluss, Lernpfad und Profil und reagiert mit Stimmung, Sprechblase und türkischem Ruf.

**Architecture:** Der Hund ist ein zustandsloses `@Composable` (`Kangal(mood, size)`), gezeichnet mit `Canvas` aus Kreisen, Ovalen und Bögen; Dauer-Animationen über `rememberInfiniteTransition`, Sprünge über `Animatable`. Die Stimmung wird aus vorhandenem Zustand abgeleitet, nur der aktuelle Ruf liegt im `LessonViewModel`. Die Rufe sind reine Daten mit Unit-Tests.

**Tech Stack:** Kotlin 2.0.21, Jetpack Compose (BOM 2024.12.01), Android TextToSpeech, JUnit 4. Keine neuen Abhängigkeiten. Spec: `docs/superpowers/specs/2026-10-02-maskottchen-kangal-design.md`.

## Global Constraints

- Keine neuen Bibliotheken, keine Bilddateien, kein Lottie – alles im Code gezeichnet.
- Figur: Kangal, sandfarbener Körper (`0xFFD9A066`), schwarze Schnauzen-Maske (`0xFF3B2F2A`), Schlappohren.
- Stimmungen exakt: `IDLE`, `HAPPY`, `SAD`, `CHEER`, `WAVE`.
- Rufe exakt wie in der Spec, türkisch groß, deutsche Übersetzung klein darunter.
- Nie zweimal direkt hintereinander derselbe Ruf.
- Gesprochen wird mit `QUEUE_ADD` (hinter dem Vokabel-Wort), nie mit `QUEUE_FLUSH`.
- Sound-Schalter aus oder keine türkische Stimme → Sprechblase ohne Ton.
- Deutsche KDoc-Kommentare, Test-Namen deutsch in Backticks (wie im Repo üblich).

## Build & Test-Umgebung

Lokal ist kein Build möglich (nur JDK 25, Gradle 8.11.1 braucht JDK 17). Prüfung läuft über GitHub Actions (`Android Build`, triggert bei Push auf `claude/**`).

„CI prüfen" bedeutet jeweils:

```bash
git push -u origin claude/maskottchen
```

```bash
gh run list -R SaitTan/TurkischLernApp --branch claude/maskottchen --limit 1
```

```bash
gh run watch -R SaitTan/TurkischLernApp <RUN_ID> --exit-status
```

## Dateien

| Datei | Aktion | Verantwortung |
|---|---|---|
| `app/src/main/java/de/turkischlernen/app/data/content/MascotPhrases.kt` | neu | Rufe + Auswahl ohne Wiederholung |
| `app/src/test/java/de/turkischlernen/app/MascotPhrasesTest.kt` | neu | Tests dazu |
| `app/src/main/java/de/turkischlernen/app/ui/mascot/MascotMood.kt` | neu | Stimmungen |
| `app/src/main/java/de/turkischlernen/app/ui/mascot/Kangal.kt` | neu | Zeichnung + Animationen |
| `app/src/main/java/de/turkischlernen/app/ui/mascot/MascotBubble.kt` | neu | Sprechblase |
| `app/src/main/java/de/turkischlernen/app/audio/SpeechManager.kt` | ändern | `queue`-Schalter |
| `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonViewModel.kt` | ändern | aktueller Ruf |
| `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonScreen.kt` | ändern | Hund + Blase unten |
| `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonCompleteScreen.kt` | ändern | jubelnder Hund |
| `app/src/main/java/de/turkischlernen/app/ui/path/PathScreen.kt` | ändern | winkender Hund |
| `app/src/main/java/de/turkischlernen/app/ui/profile/ProfileScreen.kt` | ändern | Hund statt Eule |

---

### Task 0: Branch anlegen

- [ ] **Step 1: Branch vom aktuellen Stand erstellen**

```bash
git switch -c claude/maskottchen
```

---

### Task 1: Rufe des Maskottchens (TDD)

**Files:**
- Create: `app/src/main/java/de/turkischlernen/app/data/content/MascotPhrases.kt`
- Test: `app/src/test/java/de/turkischlernen/app/MascotPhrasesTest.kt`

**Interfaces:**
- Produces:
  - `data class MascotPhrase(val tr: String, val de: String)`
  - `MascotPhrases.success: List<MascotPhrase>`, `.comfort`, `.cheer`
  - `MascotPhrases.pick(from: List<MascotPhrase>, previous: MascotPhrase?, random: kotlin.random.Random): MascotPhrase`

- [ ] **Step 1: Failing Test schreiben**

```kotlin
package de.turkischlernen.app

import de.turkischlernen.app.data.content.MascotPhrase
import de.turkischlernen.app.data.content.MascotPhrases
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MascotPhrasesTest {

    @Test
    fun `jede Kategorie hat mehrere Rufe`() {
        assertTrue(MascotPhrases.success.size >= 3)
        assertTrue(MascotPhrases.comfort.size >= 3)
        assertTrue(MascotPhrases.cheer.size >= 3)
    }

    @Test
    fun `jeder Ruf hat tuerkischen Text und deutsche Uebersetzung`() {
        val alle = MascotPhrases.success + MascotPhrases.comfort + MascotPhrases.cheer
        alle.forEach { phrase ->
            assertTrue("Türkisch fehlt: $phrase", phrase.tr.isNotBlank())
            assertTrue("Übersetzung fehlt: $phrase", phrase.de.isNotBlank())
        }
    }

    @Test
    fun `derselbe Ruf kommt nie zweimal hintereinander`() {
        val random = Random(4)
        var previous: MascotPhrase? = null
        repeat(200) {
            val phrase = MascotPhrases.pick(MascotPhrases.success, previous, random)
            assertNotEquals(previous, phrase)
            assertTrue(phrase in MascotPhrases.success)
            previous = phrase
        }
    }

    @Test
    fun `aus einer einzigen Moeglichkeit kommt genau diese`() {
        val nur = listOf(MascotPhrase("Harika!", "Toll!"))
        assertEquals(nur.first(), MascotPhrases.pick(nur, null, Random(1)))
    }
}
```

- [ ] **Step 2: Commit + CI prüfen – erwartet FAIL** (`Unresolved reference: MascotPhrases`)

```bash
git add app/src/test/java/de/turkischlernen/app/MascotPhrasesTest.kt
git commit -m "Test: Rufe des Maskottchens"
```

- [ ] **Step 3: Implementierung schreiben**

```kotlin
package de.turkischlernen.app.data.content

import kotlin.random.Random

/** Ein Zuruf des Maskottchens: türkisch mit deutscher Übersetzung. */
data class MascotPhrase(val tr: String, val de: String)

/** Was der Kangal sagt – nach Anlass sortiert. */
object MascotPhrases {

    /** Nach einer richtigen Antwort. */
    val success = listOf(
        MascotPhrase("Harika!", "Toll!"),
        MascotPhrase("Aferin!", "Gut gemacht!"),
        MascotPhrase("Çok iyi!", "Sehr gut!"),
        MascotPhrase("Süper!", "Super!"),
        MascotPhrase("Devam!", "Weiter so!")
    )

    /** Nach einer falschen Antwort – nie tadelnd. */
    val comfort = listOf(
        MascotPhrase("Olsun!", "Macht nichts!"),
        MascotPhrase("Tekrar dene!", "Versuch es nochmal!"),
        MascotPhrase("Önemli değil!", "Nicht schlimm!")
    )

    /** Am Ende einer Lektion. */
    val cheer = listOf(
        MascotPhrase("Bravo!", "Bravo!"),
        MascotPhrase("Tebrikler!", "Glückwunsch!"),
        MascotPhrase("Çok güzel!", "Sehr schön!")
    )

    /** Zufälliger Ruf aus [from], nie derselbe wie [previous]. */
    fun pick(from: List<MascotPhrase>, previous: MascotPhrase?, random: Random): MascotPhrase {
        val candidates = from.filter { it != previous }.ifEmpty { from }
        return candidates[random.nextInt(candidates.size)]
    }
}
```

- [ ] **Step 4: Commit + CI prüfen – erwartet PASS**

```bash
git add app/src/main/java/de/turkischlernen/app/data/content/MascotPhrases.kt
git commit -m "Rufe des Maskottchens (türkisch mit Übersetzung)"
```

---

### Task 2: Der gezeichnete Kangal

**Files:**
- Create: `app/src/main/java/de/turkischlernen/app/ui/mascot/MascotMood.kt`
- Create: `app/src/main/java/de/turkischlernen/app/ui/mascot/Kangal.kt`
- Create: `app/src/main/java/de/turkischlernen/app/ui/mascot/MascotBubble.kt`

**Interfaces:**
- Consumes: `MascotPhrase` (Task 1), `Modifier.popIn(visible: Boolean)` aus `ui/components/Animations.kt`
- Produces:
  - `enum class MascotMood { IDLE, HAPPY, SAD, CHEER, WAVE }`
  - `@Composable fun Kangal(mood: MascotMood, modifier: Modifier = Modifier, size: Dp = 96.dp)`
  - `@Composable fun MascotBubble(phrase: MascotPhrase?, modifier: Modifier = Modifier)`

- [ ] **Step 1: `MascotMood.kt` anlegen**

```kotlin
package de.turkischlernen.app.ui.mascot

/** Stimmungen des Maskottchens. */
enum class MascotMood {
    /** Sitzt ruhig, atmet, blinzelt. */
    IDLE,

    /** Hüpft kurz – richtige Antwort. */
    HAPPY,

    /** Ohren hängen, Kopf gesenkt – falsche Antwort. */
    SAD,

    /** Springt mehrfach – Lektion geschafft. */
    CHEER,

    /** Winkt mit einer Pfote – Lernpfad. */
    WAVE
}
```

- [ ] **Step 2: `Kangal.kt` anlegen**

```kotlin
package de.turkischlernen.app.ui.mascot

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val Fur = Color(0xFFD9A066)
private val FurDark = Color(0xFFB9834B)
private val Mask = Color(0xFF3B2F2A)
private val Belly = Color(0xFFF2DCC0)
private val Tongue = Color(0xFFEF6E8C)
private val EyeWhite = Color(0xFFFFFFFF)

/**
 * Der Kangal – komplett gezeichnet, ohne Bilddateien. Jedes Körperteil bewegt
 * sich einzeln, damit die Stimmungen erkennbar sind.
 */
@Composable
fun Kangal(
    mood: MascotMood,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    val transition = rememberInfiniteTransition(label = "kangal")

    // Atmen: der ganze Körper wird minimal größer und kleiner.
    val breath by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "breath"
    )

    // Schwanz: Grundtakt immer gleich, die Stärke hängt an der Stimmung.
    val wagPhase by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
        label = "wag"
    )
    val wagAmount = when (mood) {
        MascotMood.HAPPY, MascotMood.CHEER -> 1f
        MascotMood.SAD -> 0f
        else -> 0.35f
    }

    // Blinzeln: Augen sind fast immer offen, kurz zu.
    val eyeOpen by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 3200
                1f at 0
                1f at 2900
                0.1f at 3020
                1f at 3140
            },
            RepeatMode.Restart
        ),
        label = "blink"
    )

    // Winken: kurze Pfotenbewegung alle paar Sekunden.
    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 2600
                0f at 0
                0f at 1800
                1f at 1950
                0f at 2100
                1f at 2250
                0f at 2400
            },
            RepeatMode.Restart
        ),
        label = "wave"
    )

    // Sprung bei Erfolg bzw. Jubel.
    val hop = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        when (mood) {
            MascotMood.HAPPY -> {
                hop.animateTo(
                    1f,
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh)
                )
                hop.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }

            MascotMood.CHEER -> repeat(3) {
                hop.animateTo(1f, tween(220, easing = FastOutSlowInEasing))
                hop.animateTo(0f, tween(220, easing = FastOutSlowInEasing))
            }

            else -> hop.snapTo(0f)
        }
    }

    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val lift = hop.value * h * 0.10f

        translate(top = -lift) {
            drawBody(w, h, breath, wagPhase * wagAmount, mood)
            drawHead(w, h, eyeOpen, mood)
            if (mood == MascotMood.WAVE) drawWavingPaw(w, h, wavePhase)
        }
    }
}

/** Körper, Beine und Schwanz. */
private fun DrawScope.drawBody(w: Float, h: Float, breath: Float, wag: Float, mood: MascotMood) {
    // Schwanz hinter dem Körper, wedelt um seinen Ansatz.
    rotate(degrees = wag * 22f, pivot = Offset(w * 0.26f, h * 0.70f)) {
        drawOval(
            color = FurDark,
            topLeft = Offset(w * 0.08f, h * 0.60f),
            size = Size(w * 0.22f, h * 0.12f)
        )
    }

    // Vorderbeine
    drawOval(FurDark, Offset(w * 0.34f, h * 0.84f), Size(w * 0.12f, h * 0.14f))
    drawOval(FurDark, Offset(w * 0.54f, h * 0.84f), Size(w * 0.12f, h * 0.14f))

    // Rumpf (atmet)
    val bodyW = w * 0.56f * breath
    val bodyH = h * 0.42f * breath
    drawOval(
        color = Fur,
        topLeft = Offset(w * 0.5f - bodyW / 2f, h * 0.92f - bodyH),
        size = Size(bodyW, bodyH)
    )

    // Heller Brustfleck
    drawOval(
        color = Belly,
        topLeft = Offset(w * 0.42f, h * 0.68f),
        size = Size(w * 0.16f, h * 0.20f)
    )

    // Bei Traurigkeit sitzt der Hund etwas zusammengesunken.
    if (mood == MascotMood.SAD) {
        drawOval(
            color = FurDark,
            topLeft = Offset(w * 0.30f, h * 0.88f),
            size = Size(w * 0.40f, h * 0.06f)
        )
    }
}

/** Kopf mit Ohren, Maske, Augen, Schnauze. */
private fun DrawScope.drawHead(w: Float, h: Float, eyeOpen: Float, mood: MascotMood) {
    val headDrop = if (mood == MascotMood.SAD) h * 0.05f else 0f
    val cx = w * 0.5f
    val cy = h * 0.36f + headDrop
    val r = w * 0.26f

    // Schlappohren – bei Traurigkeit weiter unten.
    val earDrop = if (mood == MascotMood.SAD) h * 0.06f else 0f
    drawOval(
        color = FurDark,
        topLeft = Offset(cx - r * 1.25f, cy - r * 0.55f + earDrop),
        size = Size(r * 0.55f, r * 1.15f)
    )
    drawOval(
        color = FurDark,
        topLeft = Offset(cx + r * 0.70f, cy - r * 0.55f + earDrop),
        size = Size(r * 0.55f, r * 1.15f)
    )

    // Kopf
    drawCircle(color = Fur, radius = r, center = Offset(cx, cy))

    // Schwarze Maske um Schnauze und Augenpartie
    drawOval(
        color = Mask,
        topLeft = Offset(cx - r * 0.62f, cy - r * 0.10f),
        size = Size(r * 1.24f, r * 1.00f)
    )

    // Augen: offen als Kreis, beim Jubeln als lachende Bögen.
    val eyeY = cy - r * 0.22f
    val eyeDx = r * 0.38f
    if (mood == MascotMood.CHEER) {
        listOf(cx - eyeDx, cx + eyeDx).forEach { ex ->
            drawArc(
                color = EyeWhite,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(ex - r * 0.18f, eyeY - r * 0.16f),
                size = Size(r * 0.36f, r * 0.32f),
                style = Stroke(width = r * 0.10f)
            )
        }
    } else {
        listOf(cx - eyeDx, cx + eyeDx).forEach { ex ->
            drawOval(
                color = EyeWhite,
                topLeft = Offset(ex - r * 0.14f, eyeY - r * 0.14f * eyeOpen),
                size = Size(r * 0.28f, r * 0.28f * eyeOpen)
            )
            drawCircle(
                color = Mask,
                radius = r * 0.08f * eyeOpen,
                center = Offset(ex, eyeY)
            )
        }
    }

    // Nase
    drawOval(
        color = Mask,
        topLeft = Offset(cx - r * 0.14f, cy + r * 0.28f),
        size = Size(r * 0.28f, r * 0.20f)
    )

    // Mund: lacht, außer bei Traurigkeit.
    val mouthY = cy + r * 0.52f
    drawArc(
        color = Mask,
        startAngle = if (mood == MascotMood.SAD) 200f else 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(cx - r * 0.26f, if (mood == MascotMood.SAD) mouthY else mouthY - r * 0.20f),
        size = Size(r * 0.52f, r * 0.30f),
        style = Stroke(width = r * 0.07f)
    )

    // Zunge beim Freuen
    if (mood == MascotMood.HAPPY || mood == MascotMood.CHEER) {
        drawOval(
            color = Tongue,
            topLeft = Offset(cx - r * 0.12f, mouthY + r * 0.02f),
            size = Size(r * 0.24f, r * 0.26f)
        )
    }
}

/** Erhobene, winkende Pfote. */
private fun DrawScope.drawWavingPaw(w: Float, h: Float, wave: Float) {
    val pivot = Offset(w * 0.70f, h * 0.68f)
    rotate(degrees = -20f - wave * 25f, pivot = pivot) {
        drawOval(
            color = Fur,
            topLeft = Offset(w * 0.68f, h * 0.50f),
            size = Size(w * 0.14f, h * 0.22f)
        )
        drawCircle(color = Belly, radius = w * 0.055f, center = Offset(w * 0.75f, h * 0.52f))
    }
}
```

- [ ] **Step 3: `MascotBubble.kt` anlegen**

```kotlin
package de.turkischlernen.app.ui.mascot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import de.turkischlernen.app.data.content.MascotPhrase
import de.turkischlernen.app.ui.components.popIn
import de.turkischlernen.app.ui.theme.AppColors

/**
 * Sprechblase des Maskottchens: türkischer Ruf groß, deutsche Übersetzung klein.
 * Ist [phrase] null, bleibt der Platz reserviert und die Blase unsichtbar.
 */
@Composable
fun MascotBubble(phrase: MascotPhrase?, modifier: Modifier = Modifier) {
    Box(modifier.popIn(phrase != null), contentAlignment = Alignment.CenterStart) {
        Box(contentAlignment = Alignment.CenterStart) {
            // Zipfel zur Figur hin
            Canvas(Modifier.size(10.dp)) {
                val tail = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(0f, size.height / 2f)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(tail, color = AppColors.GreenLight)
            }
            Column(
                Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.GreenLight)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = phrase?.tr.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.GreenDark
                )
                Text(
                    text = phrase?.de.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.GreenDark.copy(alpha = 0.75f)
                )
            }
        }
    }
}
```

- [ ] **Step 4: Commit + CI prüfen – erwartet PASS** (kompiliert; sichtbar wird der Hund ab Task 4)

```bash
git add app/src/main/java/de/turkischlernen/app/ui/mascot
git commit -m "Kangal-Maskottchen: Zeichnung, Stimmungen und Sprechblase"
```

---

### Task 3: Stimme anhängen und Ruf im ViewModel

**Files:**
- Modify: `app/src/main/java/de/turkischlernen/app/audio/SpeechManager.kt`
- Modify: `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonViewModel.kt`

**Interfaces:**
- Consumes: `MascotPhrases`, `MascotPhrase` (Task 1)
- Produces:
  - `SpeechManager.speak(text: String, slow: Boolean = false, queue: Boolean = false)`
  - `LessonViewModel.mascotPhrase: MascotPhrase?` (null, solange keine Antwort gegeben wurde)

- [ ] **Step 1: `SpeechManager.speak` ersetzen**

```kotlin
    /**
     * Liest [text] auf Türkisch vor.
     * @param slow langsamere, deutlichere Aussprache (z. B. bei langem Antippen)
     * @param queue true = hinten anstellen, statt Laufendes abzuschneiden
     *              (der Maskottchen-Ruf kommt so nach dem Vokabel-Wort)
     */
    fun speak(text: String, slow: Boolean = false, queue: Boolean = false) {
        val engine = tts ?: return
        if (!ready || text.isBlank()) return
        engine.setSpeechRate(if (slow) SLOW_RATE else NORMAL_RATE)
        val mode = if (queue) TextToSpeech.QUEUE_ADD else TextToSpeech.QUEUE_FLUSH
        engine.speak(text, mode, null, text.hashCode().toString())
    }
```

- [ ] **Step 2: `LessonViewModel` – Imports ergänzen**

```kotlin
import de.turkischlernen.app.data.content.MascotPhrase
import de.turkischlernen.app.data.content.MascotPhrases
```

- [ ] **Step 3: `LessonViewModel` – Zustand ergänzen** (direkt nach der Eigenschaft `praise`)

```kotlin
    /** Aktueller Zuruf des Maskottchens – null, bis eine Antwort gegeben wurde. */
    var mascotPhrase by mutableStateOf<MascotPhrase?>(null)
        private set
```

- [ ] **Step 4: `submitAnswer` – Ruf setzen**

Im `if (correct) { … }`-Zweig direkt nach der Zeile, die `praise` setzt:

```kotlin
            mascotPhrase = MascotPhrases.pick(MascotPhrases.success, mascotPhrase, random)
```

Im `else { … }`-Zweig als erste Zeile:

```kotlin
            mascotPhrase = MascotPhrases.pick(MascotPhrases.comfort, mascotPhrase, random)
```

- [ ] **Step 5: `next()` – Ruf zurücksetzen**

Direkt nach `answerState = AnswerState.Waiting`:

```kotlin
        mascotPhrase = null
```

- [ ] **Step 6: `complete()` – Jubelruf setzen**

Direkt nach `finished = true`:

```kotlin
        mascotPhrase = MascotPhrases.pick(MascotPhrases.cheer, mascotPhrase, random)
```

- [ ] **Step 7: Commit + CI prüfen – erwartet PASS**

```bash
git add app/src/main/java/de/turkischlernen/app/audio/SpeechManager.kt app/src/main/java/de/turkischlernen/app/ui/lesson/LessonViewModel.kt
git commit -m "Maskottchen-Ruf im ViewModel, Stimme haengt sich an"
```

---

### Task 4: Hund in der Lektion

**Files:**
- Modify: `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonScreen.kt`

**Interfaces:**
- Consumes: `Kangal`, `MascotMood`, `MascotBubble` (Task 2), `LessonViewModel.mascotPhrase` (Task 3), `SpeechManager.speak(text, slow, queue)` (Task 3), `container.settingsRepository.current`

- [ ] **Step 1: Imports ergänzen**

```kotlin
import de.turkischlernen.app.data.content.MascotPhrase
import de.turkischlernen.app.ui.mascot.Kangal
import de.turkischlernen.app.ui.mascot.MascotBubble
import de.turkischlernen.app.ui.mascot.MascotMood
```

- [ ] **Step 2: Einstellungen lesen** – direkt nach `val progress by container.progressRepository.progress.collectAsState(initial = UserProgress())`:

```kotlin
    val settings by container.settingsRepository.current.collectAsState()
```

- [ ] **Step 3: Ruf sprechen** – direkt nach dem `LaunchedEffect`, der `glow` steuert:

```kotlin
    // Der Ruf wird hinten angestellt, damit er das Vokabel-Wort nicht abschneidet.
    LaunchedEffect(viewModel.mascotPhrase) {
        val phrase = viewModel.mascotPhrase
        if (phrase != null && settings.soundEnabled) {
            container.speech.speak(phrase.tr, slow = false, queue = true)
        }
    }
```

- [ ] **Step 4: Aufruf von `FeedbackBar` ersetzen**

```kotlin
        FeedbackBar(
            answerState = viewModel.answerState,
            praise = viewModel.praise,
            mascotMood = when (viewModel.answerState) {
                AnswerState.Correct -> MascotMood.HAPPY
                is AnswerState.Wrong -> MascotMood.SAD
                AnswerState.Waiting -> MascotMood.IDLE
            },
            mascotPhrase = viewModel.mascotPhrase,
            checkEnabled = isAnswerReady(exercise, interaction),
            onCheck = {
                val correct = isAnswerCorrect(exercise, interaction)
                viewModel.submitAnswer(
                    correct = correct,
                    correctAnswer = correctAnswerText(exercise),
                    itemIds = exercise.itemIds,
                    // Ein falsches Paar kostet kein Herz, zählt aber als Fehler.
                    loseHeart = exercise !is Exercise.MatchPairs
                )
                speak(spokenText(exercise), false)
            },
            onContinue = { viewModel.next() },
            onReplay = { speak(spokenText(exercise), true) }
        )
```

- [ ] **Step 5: `FeedbackBar` erweitern**

Signatur ersetzen:

```kotlin
@Composable
private fun FeedbackBar(
    answerState: AnswerState,
    praise: String,
    mascotMood: MascotMood,
    mascotPhrase: MascotPhrase?,
    checkEnabled: Boolean,
    onCheck: () -> Unit,
    onContinue: () -> Unit,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
```

Im `Surface { … }` den Inhalt in eine Spalte mit Hund darüber packen – aus

```kotlin
    Surface(color = background, modifier = modifier.fillMaxWidth()) {
        AnimatedContent(
```

wird

```kotlin
    Surface(color = background, modifier = modifier.fillMaxWidth()) {
        Column {
            // Maskottchen mit Sprechblase – immer sichtbar, auch während der Frage.
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Kangal(mood = mascotMood, size = 64.dp)
                Spacer(Modifier.width(8.dp))
                MascotBubble(phrase = mascotPhrase)
            }

            AnimatedContent(
```

Am Ende der Funktion eine schließende Klammer für die neue `Column` ergänzen.

- [ ] **Step 6: Commit + CI prüfen – erwartet PASS**

```bash
git add app/src/main/java/de/turkischlernen/app/ui/lesson/LessonScreen.kt
git commit -m "Lektion: Kangal mit Sprechblase in der Rueckmeldungs-Leiste"
```

---

### Task 5: Jubelnder Hund beim Abschluss

**Files:**
- Modify: `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonCompleteScreen.kt`
- Modify: `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonScreen.kt` (Aufruf erweitern)

**Interfaces:**
- Produces: `LessonCompleteScreen(..., mascotPhrase: MascotPhrase?, …)` – neuer Parameter direkt nach `resultsReady`

- [ ] **Step 1: Imports in `LessonCompleteScreen.kt` ergänzen**

```kotlin
import androidx.compose.foundation.layout.width
import de.turkischlernen.app.data.content.MascotPhrase
import de.turkischlernen.app.ui.mascot.Kangal
import de.turkischlernen.app.ui.mascot.MascotBubble
import de.turkischlernen.app.ui.mascot.MascotMood
```

- [ ] **Step 2: Parameter ergänzen** – in der Signatur direkt nach `resultsReady: Boolean,`

```kotlin
    mascotPhrase: MascotPhrase?,
```

- [ ] **Step 3: Pokal-Zeile durch Pokal + Hund ersetzen**

```kotlin
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Kangal(
                    mood = MascotMood.CHEER,
                    size = 104.dp,
                    modifier = Modifier.popIn(stage >= STAGE_TROPHY)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (perfect) "🏆" else "🎉",
                    fontSize = 96.sp,
                    modifier = Modifier.popIn(stage >= STAGE_TROPHY)
                )
            }
            Spacer(Modifier.height(8.dp))
            MascotBubble(
                phrase = if (stage >= STAGE_TITLE) mascotPhrase else null,
                modifier = Modifier.fillMaxWidth()
            )
```

- [ ] **Step 4: Ruf sprechen** – direkt nach dem `LaunchedEffect(activeOverlay) { … }`-Block

```kotlin
    val speech = LocalAppContainer.current.speech
    val soundOn = LocalAppContainer.current.settingsRepository.current.value.soundEnabled
    LaunchedEffect(mascotPhrase, stage) {
        if (stage >= STAGE_TITLE && mascotPhrase != null && soundOn) {
            speech.speak(mascotPhrase.tr, slow = false, queue = true)
        }
    }
```

- [ ] **Step 5: Aufruf in `LessonScreen` erweitern**

```kotlin
        LessonCompleteScreen(
            earnedXp = viewModel.earnedXp,
            accuracyPercent = viewModel.accuracyPercent,
            perfect = viewModel.mistakes == 0,
            streakDays = progress.streakDays,
            newAchievements = viewModel.newAchievements,
            streakIncreased = viewModel.streakIncreased,
            resultsReady = viewModel.resultsReady,
            mascotPhrase = viewModel.mascotPhrase,
            onContinue = onExit
        )
```

- [ ] **Step 6: Commit + CI prüfen – erwartet PASS**

```bash
git add app/src/main/java/de/turkischlernen/app/ui/lesson
git commit -m "Abschluss: jubelnder Kangal mit Sprechblase"
```

---

### Task 6: Hund auf Lernpfad und im Profil

**Files:**
- Modify: `app/src/main/java/de/turkischlernen/app/ui/path/PathScreen.kt`
- Modify: `app/src/main/java/de/turkischlernen/app/ui/profile/ProfileScreen.kt`

- [ ] **Step 1: Imports in `PathScreen.kt` ergänzen**

```kotlin
import de.turkischlernen.app.ui.mascot.Kangal
import de.turkischlernen.app.ui.mascot.MascotMood
```

- [ ] **Step 2: Winkenden Hund neben den aktuellen Knoten setzen** – in `LessonNode`, im `Box(Modifier.offset(x = offsetX).size(84.dp)) { … }`, vor dessen schließender Klammer:

```kotlin
            if (state == NodeState.CURRENT) {
                Kangal(
                    mood = MascotMood.WAVE,
                    size = 56.dp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 62.dp)
                )
            }
```

- [ ] **Step 3: Imports in `ProfileScreen.kt` ergänzen**

```kotlin
import de.turkischlernen.app.ui.mascot.Kangal
import de.turkischlernen.app.ui.mascot.MascotMood
```

- [ ] **Step 4: Eule durch Hund ersetzen** – im `item(key = "head")`-Block die Zeile `Text("🦉", fontSize = 66.sp)` ersetzen durch:

```kotlin
                Kangal(mood = MascotMood.IDLE, size = 110.dp)
```

- [ ] **Step 5: Commit + CI prüfen – erwartet PASS**

```bash
git add app/src/main/java/de/turkischlernen/app/ui/path/PathScreen.kt app/src/main/java/de/turkischlernen/app/ui/profile/ProfileScreen.kt
git commit -m "Lernpfad und Profil: winkender bzw. ruhender Kangal"
```

---

### Task 7: Aufs Handy bringen und prüfen

- [ ] **Step 1: APK des letzten grünen Laufs laden**

```bash
gh run download <RUN_ID> -R SaitTan/TurkischLernApp -n tuerkisch-lernen-apk -D "<SCRATCHPAD>/apk-maskottchen"
```

- [ ] **Step 2: Installieren** (Update, Fortschritt bleibt erhalten)

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r "<SCRATCHPAD>\apk-maskottchen\release\app-release.apk"
```

- [ ] **Step 3: Prüfliste mit dem Nutzer**
  - Lektion: Hund unten links sichtbar, blinzelt, Schwanz wippt
  - Richtige Antwort: hüpft, Zunge raus, Sprechblase mit türkischem Ruf, Stimme kommt **nach** dem Vokabel-Wort
  - Falsche Antwort: Ohren hängen, Kopf gesenkt, tröstender Ruf
  - Abschluss: springt dreimal, lachende Augen, Jubelruf
  - Lernpfad: sitzt neben „LOS!" und winkt alle paar Sekunden
  - Profil: Hund statt Eule
  - Sound-Schalter aus: Sprechblase bleibt, Stimme schweigt
  - Sieht der Hund wie ein Kangal aus (sandfarben, schwarze Maske, Schlappohren)? Sonst Farben und Formen in `Kangal.kt` nachziehen

- [ ] **Step 4: Pull Request**

```bash
gh pr create -R SaitTan/TurkischLernApp --base main --head claude/maskottchen --title "Etappe 2: Maskottchen (Kangal)" --body-file <PR_BODY_FILE>
```

PR-Text: Zusammenfassung der Tasks, Verweis auf Spec und Plan, Prüfliste aus Step 3, endet mit
`🤖 Generated with [Claude Code](https://claude.com/claude-code)`.
