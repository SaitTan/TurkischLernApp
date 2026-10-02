# Lektions-Gefühl (Etappe 1) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Lektionen fühlen sich wie Duolingo an – echte Sounds, Vibration, Antwort-Animationen, Combo-Serie, gestaffelter Lektions-Abschluss mit Abzeichen- und Serien-Einblendung, Schalter für Sound/Vibration.

**Architecture:** Reine Logik (Combo, Genauigkeit, neue Abzeichen, Lobspruch) in `LessonLogic` mit Unit-Tests. `SoundPlayer` (SoundPool + CC0-OGGs) und `Haptics` fragen synchron einen `SettingsRepository.current`-StateFlow ab. Animationen sind wiederverwendbare Compose-Modifier (`shake`, `bounce`, `pressScale`, `popIn`) plus `AnimatedCounter`; die Lektions-Views reagieren auf Statuswechsel.

**Tech Stack:** Kotlin 2.0.21, Jetpack Compose (BOM 2024.12.01), DataStore Preferences 1.1.1, Android `SoundPool`/`VibratorManager`, JUnit 4. Spec: `docs/superpowers/specs/2026-09-15-lektions-gefuehl-design.md`.

## Global Constraints

- minSdk 33, compileSdk/targetSdk 35 – keine neuen Bibliotheken, kein Lottie.
- Keine Assets/Figuren/Sounds von Duolingo; nur Kenney-CC0-Sounds, Quelle in `LICENSES.md`.
- Zielgruppe Kinder 8–12: Fehler klar, aber freundlich.
- Lobsprüche exakt: „Super!“, „Stark!“, „Klasse!“, „Richtig!“, „Harika!“, „Aferin!“, „Çok iyi!“ – nie zweimal direkt hintereinander derselbe.
- Combo-Banner ab 3 richtigen in Folge; Stufe = min(combo − 2, 5).
- Genauigkeit = richtige Antworten / alle Antworten, gerundet; ohne Antworten 100 %.
- Sound- und Vibrations-Schalter im Eltern-Bereich, Standard an; getrennter DataStore `einstellungen`, vom Fortschritts-Reset unberührt; TTS nicht betroffen.
- Fehler beim Laden/Abspielen von Sounds oder fehlender Vibrator → still, kein Absturz.
- Code-Stil wie im Repo: deutsche KDoc-Kommentare, Test-Namen in Backticks auf Deutsch.

## Build & Test-Umgebung

Lokal ist **kein** Build möglich (nur JDK 25 von Android Studio vorhanden, Gradle 8.11.1 braucht JDK 17; `platforms;android-35` fehlt). Tests und APK laufen über GitHub Actions (`.github/workflows/android.yml`, triggert bei Push auf `claude/**`).

Standard-Prüfung nach jedem Task („CI prüfen“):

```bash
git push -u origin claude/lektions-gefuehl
```

```bash
gh run list -R SaitTan/TurkischLernApp --branch claude/lektions-gefuehl --limit 1
```

```bash
gh run watch -R SaitTan/TurkischLernApp <RUN_ID> --exit-status
```

Bei Fehlschlag: `gh run view -R SaitTan/TurkischLernApp <RUN_ID> --log-failed`.

## Dateien

| Datei | Aktion | Verantwortung |
|---|---|---|
| `app/src/main/java/de/turkischlernen/app/data/progress/LessonLogic.kt` | neu | reine Lektions-Logik |
| `app/src/test/java/de/turkischlernen/app/LessonLogicTest.kt` | neu | Tests dazu |
| `app/src/main/java/de/turkischlernen/app/data/settings/SettingsRepository.kt` | neu | Sound/Vibration-Einstellungen |
| `app/src/main/java/de/turkischlernen/app/audio/Haptics.kt` | neu | Vibration |
| `app/src/main/java/de/turkischlernen/app/audio/SoundPlayer.kt` | ersetzen | SoundPool-Sounds |
| `app/src/main/res/raw/sfx_*.ogg` (8 Dateien) | neu | Sounds |
| `LICENSES.md` | neu | Quellenangabe |
| `app/src/main/AndroidManifest.xml` | ändern | VIBRATE-Permission |
| `app/src/main/java/de/turkischlernen/app/AppContainer.kt` | ändern | Verdrahtung |
| `app/src/main/java/de/turkischlernen/app/ui/components/Animations.kt` | neu | Animations-Modifier, Zähler |
| `app/src/main/java/de/turkischlernen/app/ui/components/StatsBar.kt` | ändern | animierter Balken + Glühen |
| `app/src/main/java/de/turkischlernen/app/ui/lesson/ExerciseViews.kt` | ändern | Karten/Kacheln animiert + Tap-Sound |
| `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonViewModel.kt` | ändern | Combo, Lob, Genauigkeit, Ergebnisse |
| `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonScreen.kt` | ersetzen | Feedback-Leiste, Combo-Banner, Herz |
| `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonCompleteScreen.kt` | ersetzen | gestaffelter Abschluss |
| `app/src/main/java/de/turkischlernen/app/ui/profile/ProfileScreen.kt` | ändern | Schalter |

---

### Task 0: Branch anlegen

- [ ] **Step 1: Branch von `main` erstellen** (enthält den Spec-Commit)

```bash
git switch -c claude/lektions-gefuehl
```

---

### Task 1: LessonLogic (reine Logik, TDD)

**Files:**
- Create: `app/src/main/java/de/turkischlernen/app/data/progress/LessonLogic.kt`
- Test: `app/src/test/java/de/turkischlernen/app/LessonLogicTest.kt`

**Interfaces:**
- Consumes: `data class Achievement(id, emoji, title, description, unlocked)` aus `data/progress/Achievements.kt`
- Produces:
  - `LessonLogic.PRAISES: List<String>`
  - `LessonLogic.nextCombo(current: Int, correct: Boolean): Int`
  - `LessonLogic.showsComboBanner(combo: Int): Boolean`
  - `LessonLogic.comboLevel(combo: Int): Int` (0 unter Schwelle, sonst 1..5)
  - `LessonLogic.accuracyPercent(correct: Int, total: Int): Int`
  - `LessonLogic.newlyUnlocked(before: List<Achievement>, after: List<Achievement>): List<Achievement>`
  - `LessonLogic.pickPraise(previous: String?, random: kotlin.random.Random): String`
  - `LessonLogic.xpTickCount(xp: Int): Int` (0..10)

- [ ] **Step 1: Failing Test schreiben**

```kotlin
package de.turkischlernen.app

import de.turkischlernen.app.data.progress.Achievement
import de.turkischlernen.app.data.progress.LessonLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class LessonLogicTest {

    @Test
    fun `richtige Antwort erhoeht die Combo`() {
        assertEquals(3, LessonLogic.nextCombo(2, correct = true))
    }

    @Test
    fun `falsche Antwort setzt die Combo zurueck`() {
        assertEquals(0, LessonLogic.nextCombo(7, correct = false))
    }

    @Test
    fun `Combo-Banner erscheint erst ab drei in Folge`() {
        assertFalse(LessonLogic.showsComboBanner(2))
        assertTrue(LessonLogic.showsComboBanner(3))
    }

    @Test
    fun `Combo-Stufe steigt bis maximal fuenf`() {
        assertEquals(0, LessonLogic.comboLevel(2))
        assertEquals(1, LessonLogic.comboLevel(3))
        assertEquals(2, LessonLogic.comboLevel(4))
        assertEquals(5, LessonLogic.comboLevel(7))
        assertEquals(5, LessonLogic.comboLevel(20))
    }

    @Test
    fun `Genauigkeit wird gerundet`() {
        assertEquals(67, LessonLogic.accuracyPercent(correct = 2, total = 3))
        assertEquals(100, LessonLogic.accuracyPercent(correct = 5, total = 5))
        assertEquals(0, LessonLogic.accuracyPercent(correct = 0, total = 4))
    }

    @Test
    fun `ohne Antworten ist die Genauigkeit 100 Prozent`() {
        assertEquals(100, LessonLogic.accuracyPercent(correct = 0, total = 0))
    }

    @Test
    fun `nur neu freigeschaltete Abzeichen werden gemeldet`() {
        val before = listOf(badge("x", true), badge("y", false), badge("z", false))
        val after = listOf(badge("x", true), badge("y", true), badge("z", false))
        assertEquals(listOf("y"), LessonLogic.newlyUnlocked(before, after).map { it.id })
    }

    @Test
    fun `Lobspruch wiederholt sich nie direkt`() {
        val random = Random(42)
        var previous: String? = null
        repeat(200) {
            val praise = LessonLogic.pickPraise(previous, random)
            assertNotEquals(previous, praise)
            assertTrue(praise in LessonLogic.PRAISES)
            previous = praise
        }
    }

    @Test
    fun `XP-Ticks sind auf zehn begrenzt`() {
        assertEquals(0, LessonLogic.xpTickCount(0))
        assertEquals(5, LessonLogic.xpTickCount(5))
        assertEquals(10, LessonLogic.xpTickCount(25))
    }

    private fun badge(id: String, unlocked: Boolean) =
        Achievement(id, "⭐", id, id, unlocked)
}
```

- [ ] **Step 2: Commit + CI prüfen – erwartet FAIL** (Kompilierfehler `Unresolved reference: LessonLogic` im Schritt „Unit-Tests“)

```bash
git add app/src/test/java/de/turkischlernen/app/LessonLogicTest.kt
git commit -m "Test: LessonLogic (Combo, Genauigkeit, Abzeichen, Lobspruch)"
```

Dann „CI prüfen“ (siehe oben).

- [ ] **Step 3: Implementierung schreiben**

```kotlin
package de.turkischlernen.app.data.progress

import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Rechenlogik rund um eine laufende Lektion – ohne Android-Abhängigkeiten,
 * damit sie in Unit-Tests geprüft werden kann.
 */
object LessonLogic {

    /** Ab so vielen richtigen Antworten in Folge erscheint das Combo-Banner. */
    const val COMBO_THRESHOLD = 3

    private const val MAX_COMBO_LEVEL = 5
    private const val MAX_XP_TICKS = 10

    val PRAISES = listOf("Super!", "Stark!", "Klasse!", "Richtig!", "Harika!", "Aferin!", "Çok iyi!")

    fun nextCombo(current: Int, correct: Boolean): Int = if (correct) current + 1 else 0

    fun showsComboBanner(combo: Int): Boolean = combo >= COMBO_THRESHOLD

    /** 0 unterhalb der Schwelle, danach 1 bis 5 – steuert die Tonhöhe. */
    fun comboLevel(combo: Int): Int =
        if (combo < COMBO_THRESHOLD) 0 else (combo - COMBO_THRESHOLD + 1).coerceAtMost(MAX_COMBO_LEVEL)

    fun accuracyPercent(correct: Int, total: Int): Int =
        if (total == 0) 100 else (correct * 100.0 / total).roundToInt()

    /** Abzeichen, die in [after] freigeschaltet sind, in [before] aber noch nicht. */
    fun newlyUnlocked(before: List<Achievement>, after: List<Achievement>): List<Achievement> {
        val unlockedBefore = before.filter { it.unlocked }.map { it.id }.toSet()
        return after.filter { it.unlocked && it.id !in unlockedBefore }
    }

    /** Zufälliger Lobspruch, nie derselbe wie [previous]. */
    fun pickPraise(previous: String?, random: Random): String {
        val candidates = PRAISES.filter { it != previous }
        return candidates[random.nextInt(candidates.size)]
    }

    /** Anzahl der Tick-Sounds beim Hochzählen der XP. */
    fun xpTickCount(xp: Int): Int = xp.coerceIn(0, MAX_XP_TICKS)
}
```

- [ ] **Step 4: Commit + CI prüfen – erwartet PASS** (alle Tests grün, APK gebaut)

```bash
git add app/src/main/java/de/turkischlernen/app/data/progress/LessonLogic.kt
git commit -m "LessonLogic: Combo, Genauigkeit, neue Abzeichen, Lobspruch"
```

---

### Task 2: Einstellungen, Vibration, echte Sounds

**Files:**
- Create: `app/src/main/java/de/turkischlernen/app/data/settings/SettingsRepository.kt`
- Create: `app/src/main/java/de/turkischlernen/app/audio/Haptics.kt`
- Replace: `app/src/main/java/de/turkischlernen/app/audio/SoundPlayer.kt`
- Create: `app/src/main/res/raw/sfx_tap.ogg`, `sfx_correct.ogg`, `sfx_wrong.ogg`, `sfx_combo.ogg`, `sfx_xp_tick.ogg`, `sfx_celebrate.ogg`, `sfx_badge.ogg`, `sfx_streak.ogg`
- Create: `LICENSES.md`
- Modify: `app/src/main/AndroidManifest.xml`, `app/src/main/java/de/turkischlernen/app/AppContainer.kt`

**Interfaces:**
- Produces:
  - `data class AppSettings(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true)`
  - `SettingsRepository(context).current: StateFlow<AppSettings>`, `suspend setSoundEnabled(Boolean)`, `suspend setHapticsEnabled(Boolean)`
  - `Haptics(context, enabled: () -> Boolean)`: `success()`, `error()`
  - `SoundPlayer(context, enabled: () -> Boolean)`: `tap()`, `correct()`, `wrong()`, `combo(level: Int)`, `xpTick()`, `celebrate()`, `badge()`, `streak()`, `release()`
  - `AppContainer.settingsRepository`, `AppContainer.haptics`, `AppContainer.sounds`

- [ ] **Step 1: Sounds ins Projekt kopieren** (Pakete liegen bereits entpackt im Scratchpad; falls nicht: `https://kenney.nl/assets/interface-sounds` und `https://kenney.nl/assets/music-jingles`)

```bash
SCRATCH="C:/Users/tans/AppData/Local/Temp/claude/C--Users-tans-OneDrive---DB-E-C-O--Group-Dokumente-GitHub-TurkischLernApp/a36fd7f4-f34d-42a9-8969-236e981147a7/scratchpad"
SFX="$SCRATCH/sounds/Audio"; JIN="$SCRATCH/jingles/Audio"; RAW="app/src/main/res/raw"
mkdir -p "$RAW"
cp "$SFX/select_001.ogg" "$RAW/sfx_tap.ogg"
cp "$SFX/confirmation_001.ogg" "$RAW/sfx_correct.ogg"
cp "$SFX/error_004.ogg" "$RAW/sfx_wrong.ogg"
cp "$SFX/glass_002.ogg" "$RAW/sfx_combo.ogg"
cp "$SFX/tick_002.ogg" "$RAW/sfx_xp_tick.ogg"
cp "$JIN/Pizzicato jingles/jingles_PIZZI01.ogg" "$RAW/sfx_celebrate.ogg"
cp "$JIN/Steel jingles/jingles_STEEL00.ogg" "$RAW/sfx_badge.ogg"
cp "$JIN/Hit jingles/jingles_HIT11.ogg" "$RAW/sfx_streak.ogg"
ls -la "$RAW"
```

Expected: 8 Dateien, zusammen < 150 KB.

- [ ] **Step 2: `LICENSES.md` anlegen**

```markdown
# Lizenzen verwendeter Inhalte

## Sounds (`app/src/main/res/raw/sfx_*.ogg`)

Von Kenney (www.kenney.nl), Lizenz: Creative Commons Zero (CC0),
http://creativecommons.org/publicdomain/zero/1.0/

| Datei | Original | Paket |
|---|---|---|
| sfx_tap.ogg | select_001.ogg | Interface Sounds |
| sfx_correct.ogg | confirmation_001.ogg | Interface Sounds |
| sfx_wrong.ogg | error_004.ogg | Interface Sounds |
| sfx_combo.ogg | glass_002.ogg | Interface Sounds |
| sfx_xp_tick.ogg | tick_002.ogg | Interface Sounds |
| sfx_celebrate.ogg | jingles_PIZZI01.ogg | Music Jingles |
| sfx_badge.ogg | jingles_STEEL00.ogg | Music Jingles |
| sfx_streak.ogg | jingles_HIT11.ogg | Music Jingles |
```

- [ ] **Step 3: `SettingsRepository.kt` anlegen**

```kotlin
package de.turkischlernen.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "einstellungen")

data class AppSettings(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

/**
 * Geräte-Einstellungen aus dem Eltern-Bereich. Bewusst getrennt vom
 * Lernfortschritt, damit "Fortschritt zurücksetzen" sie nicht löscht.
 */
class SettingsRepository(context: Context) {

    private val store = context.applicationContext.settingsStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object Keys {
        val sound = booleanPreferencesKey("sound_enabled")
        val haptics = booleanPreferencesKey("haptics_enabled")
    }

    /** Synchron lesbar, damit Sounds und Vibration ohne Verzögerung prüfen können. */
    val current: StateFlow<AppSettings> = store.data
        .map { prefs ->
            AppSettings(
                soundEnabled = prefs[Keys.sound] ?: true,
                hapticsEnabled = prefs[Keys.haptics] ?: true
            )
        }
        .catch { emit(AppSettings()) }
        .stateIn(scope, SharingStarted.Eagerly, AppSettings())

    suspend fun setSoundEnabled(enabled: Boolean) {
        store.edit { prefs -> prefs[Keys.sound] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        store.edit { prefs -> prefs[Keys.haptics] = enabled }
    }
}
```

- [ ] **Step 4: `Haptics.kt` anlegen**

```kotlin
package de.turkischlernen.app.audio

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Kurze Vibration als Rückmeldung – entfällt still, wenn das Gerät keine hat. */
class Haptics(context: Context, private val enabled: () -> Boolean) {

    private val vibrator: Vibrator? =
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator

    /** Kurzes Antippen bei einer richtigen Antwort. */
    fun success() = vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))

    /** Doppelte Vibration bei einer falschen Antwort. */
    fun error() = vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 70, 40), -1))

    private fun vibrate(effect: VibrationEffect) {
        if (!enabled()) return
        val target = vibrator ?: return
        if (!target.hasVibrator()) return
        runCatching { target.vibrate(effect) }
    }
}
```

- [ ] **Step 5: `SoundPlayer.kt` komplett ersetzen**

```kotlin
package de.turkischlernen.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import de.turkischlernen.app.R
import java.util.concurrent.ConcurrentHashMap

/**
 * Feedback-Sounds (CC0 von Kenney, siehe LICENSES.md). Lädt alle Sounds beim
 * Start vor, damit sie ohne Verzögerung klingen. Nicht geladene Sounds bleiben still.
 */
class SoundPlayer(context: Context, private val enabled: () -> Boolean) {

    private enum class Sound(@RawRes val res: Int) {
        TAP(R.raw.sfx_tap),
        CORRECT(R.raw.sfx_correct),
        WRONG(R.raw.sfx_wrong),
        COMBO(R.raw.sfx_combo),
        XP_TICK(R.raw.sfx_xp_tick),
        CELEBRATE(R.raw.sfx_celebrate),
        BADGE(R.raw.sfx_badge),
        STREAK(R.raw.sfx_streak)
    }

    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val loaded: MutableSet<Int> = ConcurrentHashMap.newKeySet()

    init {
        // Listener vor dem Laden setzen, sonst gehen Meldungen verloren.
        pool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loaded += sampleId
        }
    }

    private val sampleIds: Map<Sound, Int> = Sound.entries.associateWith { sound ->
        runCatching { pool.load(context, sound.res, 1) }.getOrDefault(0)
    }

    fun tap() = play(Sound.TAP, volume = 0.5f)
    fun correct() = play(Sound.CORRECT)
    fun wrong() = play(Sound.WRONG)

    /** Combo-Ton – je höher die Stufe (1..5), desto höher der Klang. */
    fun combo(level: Int) = play(Sound.COMBO, rate = 1f + 0.1f * (level - 1).coerceIn(0, 4))

    fun xpTick() = play(Sound.XP_TICK, volume = 0.6f)
    fun celebrate() = play(Sound.CELEBRATE)
    fun badge() = play(Sound.BADGE)
    fun streak() = play(Sound.STREAK)

    private fun play(sound: Sound, volume: Float = 1f, rate: Float = 1f) {
        if (!enabled()) return
        val id = sampleIds[sound] ?: return
        if (id !in loaded) return
        runCatching { pool.play(id, volume, volume, 1, 0, rate) }
    }

    fun release() {
        pool.release()
    }
}
```

- [ ] **Step 6: Manifest – VIBRATE-Permission** (direkt nach `<manifest …>` einfügen, vor `<queries>`)

```xml
    <!-- Kurze Vibration bei richtigen/falschen Antworten. -->
    <uses-permission android:name="android.permission.VIBRATE" />

```

- [ ] **Step 7: `AppContainer.kt` ersetzen**

```kotlin
package de.turkischlernen.app

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import de.turkischlernen.app.audio.Haptics
import de.turkischlernen.app.audio.SoundPlayer
import de.turkischlernen.app.audio.SpeechManager
import de.turkischlernen.app.data.progress.ProgressRepository
import de.turkischlernen.app.data.settings.SettingsRepository

/**
 * Einfache, manuelle Dependency-Verwaltung (kein DI-Framework nötig).
 * Lebt so lange wie der Prozess.
 */
class AppContainer(context: Context) {
    val progressRepository: ProgressRepository = ProgressRepository(context)
    val settingsRepository: SettingsRepository = SettingsRepository(context)
    val speech: SpeechManager = SpeechManager(context)
    val sounds: SoundPlayer = SoundPlayer(context) { settingsRepository.current.value.soundEnabled }
    val haptics: Haptics = Haptics(context) { settingsRepository.current.value.hapticsEnabled }

    fun release() {
        speech.shutdown()
        sounds.release()
    }
}

val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("AppContainer wurde nicht bereitgestellt")
}
```

Hinweis: `LessonViewModel` ruft weiterhin `sounds.correct()`, `sounds.wrong()`, `sounds.celebrate()` – diese Methoden existieren weiter, der Build bleibt grün.

- [ ] **Step 8: Commit + CI prüfen – erwartet PASS**

```bash
git add LICENSES.md app/src/main/res/raw app/src/main/AndroidManifest.xml app/src/main/java/de/turkischlernen/app/AppContainer.kt app/src/main/java/de/turkischlernen/app/audio app/src/main/java/de/turkischlernen/app/data/settings
git commit -m "Echte Sounds (Kenney CC0), Vibration und Einstellungen für beides"
```

---

### Task 3: Animations-Bausteine + animierte Karten, Kacheln, Balken

**Files:**
- Create: `app/src/main/java/de/turkischlernen/app/ui/components/Animations.kt`
- Modify: `app/src/main/java/de/turkischlernen/app/ui/components/StatsBar.kt:72-94` (`ThickProgressBar`)
- Modify: `app/src/main/java/de/turkischlernen/app/ui/lesson/ExerciseViews.kt` (`OptionCard`, `WordBankView`, `Tile`, Imports)

**Interfaces:**
- Consumes: `AppContainer.sounds.tap()` (Task 2)
- Produces:
  - `Modifier.shake(trigger: Int): Modifier` – wackelt bei jedem neuen Wert > 0
  - `Modifier.bounce(trigger: Int): Modifier` – hüpft bei jedem neuen Wert > 0
  - `Modifier.pressScale(interactionSource: InteractionSource, pressedScale: Float = 0.95f): Modifier`
  - `Modifier.popIn(visible: Boolean): Modifier` – federt ein/aus, ohne Layout-Platz zu ändern
  - `@Composable AnimatedCounter(target: Int, start: Boolean, modifier: Modifier = Modifier, style: TextStyle = LocalTextStyle.current, color: Color = Color.Unspecified, suffix: String = "", durationMillis: Int = 900, ticks: Int = 0, onTick: () -> Unit = {})`
  - `ThickProgressBar(fraction, modifier, color, trackColor, glow: Boolean = false)`
  - `WordBankView(exercise, interaction, locked, onSpeak, wrong: Boolean = false, modifier)`

- [ ] **Step 1: `Animations.kt` anlegen**

```kotlin
package de.turkischlernen.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import kotlin.math.roundToInt

/** Wackelt kurz waagerecht, sobald [trigger] einen neuen Wert größer 0 bekommt. */
fun Modifier.shake(trigger: Int): Modifier = composed {
    val offset = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        offset.snapTo(0f)
        offset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 400
                -14f at 50
                14f at 120
                -10f at 190
                10f at 260
                -4f at 330
            }
        )
    }
    graphicsLayer { translationX = offset.value * density }
}

/** Hüpft kurz, sobald [trigger] einen neuen Wert größer 0 bekommt. */
fun Modifier.bounce(trigger: Int): Modifier = composed {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        scale.snapTo(1f)
        scale.animateTo(1.08f, tween(110))
        scale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )
    }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/** Gibt beim Drücken leicht nach und federt zurück. */
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.95f
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pressScale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Federt beim Sichtbarwerden ein. Der Platz im Layout bleibt immer reserviert,
 * damit nichts springt.
 */
fun Modifier.popIn(visible: Boolean): Modifier = composed {
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "popIn"
    )
    graphicsLayer {
        alpha = progress.coerceIn(0f, 1f)
        val scale = 0.5f + 0.5f * progress
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Zahl, die von 0 auf [target] hochzählt, sobald [start] true wird.
 * [onTick] wird dabei gleichmäßig verteilt [ticks]-mal aufgerufen (z. B. für Sounds).
 */
@Composable
fun AnimatedCounter(
    target: Int,
    start: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    suffix: String = "",
    durationMillis: Int = 900,
    ticks: Int = 0,
    onTick: () -> Unit = {}
) {
    val value = remember { Animatable(0f) }
    LaunchedEffect(target, start) {
        if (!start) return@LaunchedEffect
        var lastTick = 0
        value.animateTo(
            targetValue = target.toFloat(),
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        ) {
            if (ticks > 0 && target > 0) {
                val reached = (this.value / target * ticks).toInt()
                if (reached > lastTick) {
                    lastTick = reached
                    onTick()
                }
            }
        }
    }
    Text(
        text = "${value.value.roundToInt()}$suffix",
        style = style,
        color = color,
        modifier = modifier
    )
}
```

- [ ] **Step 2: `ThickProgressBar` in `StatsBar.kt` ersetzen** (Zeilen 72–94) und Imports ergänzen

Neue Imports (zu den bestehenden):

```kotlin
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
```

Neue Funktion:

```kotlin
/** Dicker, runder Fortschrittsbalken (Lektion / Tagesziel). [glow] färbt ihn kurz golden. */
@Composable
fun ThickProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    color: Color = AppColors.Green,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    glow: Boolean = false
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "progress"
    )
    val barColor by animateColorAsState(
        targetValue = if (glow) AppColors.Gold else color,
        animationSpec = tween(250),
        label = "progressGlow"
    )
    Box(
        modifier
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(trackColor)
    ) {
        Box(
            Modifier
                // Die Feder schwingt leicht über – fillMaxWidth verlangt 0..1.
                .fillMaxWidth(animatedFraction.coerceIn(0f, 1f))
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(barColor)
        )
    }
}
```

- [ ] **Step 3: `OptionCard` in `ExerciseViews.kt` ersetzen** (Zeilen 45–85)

Neue Imports (zu den bestehenden):

```kotlin
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableIntStateOf
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.ui.components.bounce
import de.turkischlernen.app.ui.components.pressScale
import de.turkischlernen.app.ui.components.shake
```

```kotlin
@Composable
fun OptionCard(
    text: String,
    status: OptionStatus,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val sounds = LocalAppContainer.current.sounds
    val interactionSource = remember { MutableInteractionSource() }

    // Wechsel auf richtig/falsch löst Hüpfen bzw. Wackeln aus.
    var shakeTrigger by remember { mutableIntStateOf(0) }
    var bounceTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(status) {
        when (status) {
            OptionStatus.WRONG -> shakeTrigger++
            OptionStatus.CORRECT -> bounceTrigger++
            else -> Unit
        }
    }

    val border = when (status) {
        OptionStatus.NORMAL -> MaterialTheme.colorScheme.outline
        OptionStatus.SELECTED -> AppColors.Blue
        OptionStatus.CORRECT -> AppColors.Green
        OptionStatus.WRONG -> AppColors.Red
    }
    val fill = when (status) {
        OptionStatus.NORMAL -> Color.Transparent
        OptionStatus.SELECTED -> AppColors.BlueLight
        OptionStatus.CORRECT -> AppColors.GreenLight
        OptionStatus.WRONG -> AppColors.RedLight
    }
    val textColor = when (status) {
        OptionStatus.NORMAL -> MaterialTheme.colorScheme.onBackground
        OptionStatus.SELECTED -> AppColors.BlueDark
        OptionStatus.CORRECT -> AppColors.GreenDark
        OptionStatus.WRONG -> AppColors.RedDark
    }

    Box(
        modifier
            .fillMaxWidth()
            .shake(shakeTrigger)
            .bounce(bounceTrigger)
            .pressScale(interactionSource)
            .heightIn(min = 62.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(fill)
            .border(2.dp, border, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled
            ) {
                sounds.tap()
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = textColor)
    }
}
```

- [ ] **Step 4: `WordBankView` ersetzen** (Zeilen 260–316)

```kotlin
/** Satz aus Wortkacheln bauen. [wrong] = Antwort wurde als falsch gewertet. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordBankView(
    exercise: Exercise.WordBank,
    interaction: ExerciseInteraction,
    locked: Boolean,
    onSpeak: (String, Boolean) -> Unit,
    wrong: Boolean = false,
    modifier: Modifier = Modifier
) {
    var rowShake by remember(exercise) { mutableIntStateOf(0) }
    var rowBounce by remember(exercise) { mutableIntStateOf(0) }
    LaunchedEffect(locked, wrong) {
        if (locked) {
            if (wrong) rowShake++ else rowBounce++
        }
    }

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(exercise.target.emoji, fontSize = 40.sp)
            Spacer(Modifier.size(12.dp))
            Text(
                "„${exercise.target.de}“",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.height(18.dp))

        // Antwortzeile
        Box(
            Modifier
                .fillMaxWidth()
                .shake(rowShake)
                .bounce(rowBounce)
                .heightIn(min = 76.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp)
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                interaction.chosenTiles.forEach { index ->
                    Tile(text = exercise.tiles[index], enabled = !locked) {
                        interaction.chosenTiles.remove(index)
                    }
                }
            }
        }
        Spacer(Modifier.height(18.dp))

        // Wortvorrat
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exercise.tiles.forEachIndexed { index, tile ->
                if (index !in interaction.chosenTiles) {
                    Tile(text = tile, enabled = !locked) {
                        interaction.chosenTiles.add(index)
                        onSpeak(tile, false)
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 5: `Tile` ersetzen** (Zeilen 318–331)

```kotlin
@Composable
private fun Tile(text: String, enabled: Boolean, onClick: () -> Unit) {
    val sounds = LocalAppContainer.current.sounds
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier
            .padding(vertical = 4.dp)
            .pressScale(interactionSource)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled
            ) {
                sounds.tap()
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}
```

- [ ] **Step 6: Commit + CI prüfen – erwartet PASS**

```bash
git add app/src/main/java/de/turkischlernen/app/ui/components app/src/main/java/de/turkischlernen/app/ui/lesson/ExerciseViews.kt
git commit -m "Animationen: Wackeln, Hüpfen, Drück-Effekt, Zähler; animierte Karten und Balken"
```

---

### Task 4: LessonViewModel – Combo, Lob, Genauigkeit, Ergebnisse

**Files:**
- Replace: `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonViewModel.kt`

**Interfaces:**
- Consumes: `LessonLogic.*` (Task 1), `SoundPlayer`, `Haptics` (Task 2), `Achievements.forProgress(UserProgress)`, `ProgressRepository.progress: Flow<UserProgress>`
- Produces (für Task 5/6):
  - `LessonViewModel.factory(repository, sounds, haptics, lessonId, practiceItemIds)`
  - `combo: Int`, `praise: String`, `accuracyPercent: Int`, `newAchievements: List<Achievement>`, `streakIncreased: Boolean`, `resultsReady: Boolean`
  - `onPairMismatch()` (ersetzt `playWrongSound()`)
  - Abschluss-Sound wird **nicht** mehr im ViewModel gespielt (macht der Abschluss-Bildschirm).

- [ ] **Step 1: Datei komplett ersetzen**

```kotlin
package de.turkischlernen.app.ui.lesson

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.turkischlernen.app.audio.Haptics
import de.turkischlernen.app.audio.SoundPlayer
import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.content.ExerciseGenerator
import de.turkischlernen.app.data.model.Exercise
import de.turkischlernen.app.data.progress.Achievement
import de.turkischlernen.app.data.progress.Achievements
import de.turkischlernen.app.data.progress.LessonLogic
import de.turkischlernen.app.data.progress.ProgressLogic
import de.turkischlernen.app.data.progress.ProgressRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Rückmeldung nach einer beantworteten Aufgabe. */
sealed interface AnswerState {
    data object Waiting : AnswerState
    data object Correct : AnswerState
    data class Wrong(val correctAnswer: String) : AnswerState
}

/**
 * Steuert den Ablauf einer Lektion: Aufgabenreihenfolge, Herzen, XP, Combo und das
 * Wiedervorlegen falsch beantworteter Aufgaben am Ende der Runde.
 */
class LessonViewModel(
    private val repository: ProgressRepository,
    private val sounds: SoundPlayer,
    private val haptics: Haptics,
    val lessonId: String,
    practiceItemIds: List<String> = emptyList(),
    private val random: Random = Random.Default
) : ViewModel() {

    private val lesson = Curriculum.lesson(lessonId)

    private val queue = mutableStateListOf<Exercise>()

    var solvedCount by mutableIntStateOf(0)
        private set

    var mistakes by mutableIntStateOf(0)
        private set

    var answerState by mutableStateOf<AnswerState>(AnswerState.Waiting)
        private set

    var finished by mutableStateOf(false)
        private set

    var earnedXp by mutableIntStateOf(0)
        private set

    /** Richtige Antworten in Folge. */
    var combo by mutableIntStateOf(0)
        private set

    /** Lobspruch der letzten richtigen Antwort. */
    var praise by mutableStateOf(LessonLogic.PRAISES.first())
        private set

    private var correctAnswers by mutableIntStateOf(0)
    private var totalAnswers by mutableIntStateOf(0)

    val accuracyPercent: Int get() = LessonLogic.accuracyPercent(correctAnswers, totalAnswers)

    /** Durch diese Lektion neu freigeschaltete Abzeichen (nach dem Speichern gesetzt). */
    var newAchievements by mutableStateOf<List<Achievement>>(emptyList())
        private set

    /** Die Serie ist durch diese Lektion gewachsen. */
    var streakIncreased by mutableStateOf(false)
        private set

    /** Abzeichen und Serie sind berechnet. */
    var resultsReady by mutableStateOf(false)
        private set

    /** Aufgaben, die wegen eines Fehlers erneut kommen. */
    private val repeatQueue = mutableListOf<Exercise>()

    /** Vokabeln dieser Runde – werden am Ende als "gelernt" gespeichert. */
    private val practicedIds: List<String> = practiceItemIds

    private var lastPraise: String? = null

    val isPractice: Boolean = lesson == null

    val title: String = lesson?.title ?: "Wiederholen"

    init {
        val generated = if (lesson != null) {
            ExerciseGenerator.forLesson(lesson)
        } else {
            ExerciseGenerator.forPractice(practiceItemIds)
        }
        queue.addAll(generated)
    }

    val current: Exercise? get() = queue.firstOrNull()

    val totalExercises: Int get() = solvedCount + queue.size + repeatQueue.size

    val progressFraction: Float
        get() = if (totalExercises == 0) 1f else solvedCount.toFloat() / totalExercises

    /** Antwort auswerten. [itemIds] sind die geübten Vokabeln. */
    fun submitAnswer(correct: Boolean, correctAnswer: String, itemIds: List<String>) {
        if (answerState != AnswerState.Waiting) return

        totalAnswers++
        combo = LessonLogic.nextCombo(combo, correct)

        if (correct) {
            correctAnswers++
            praise = LessonLogic.pickPraise(lastPraise, random).also { lastPraise = it }
            // Ab der Combo-Schwelle ersetzt der steigende Combo-Ton den normalen Ton.
            if (LessonLogic.showsComboBanner(combo)) {
                sounds.combo(LessonLogic.comboLevel(combo))
            } else {
                sounds.correct()
            }
            haptics.success()
            answerState = AnswerState.Correct
            viewModelScope.launch {
                itemIds.forEach { repository.clearMistake(it) }
            }
        } else {
            sounds.wrong()
            haptics.error()
            mistakes++
            answerState = AnswerState.Wrong(correctAnswer)
            current?.let { repeatQueue.add(it) }
            viewModelScope.launch {
                itemIds.forEach { repository.addMistake(it) }
                repository.loseHeart()
            }
        }
    }

    /** Falsches Paar bei "Paare finden" – Signal ohne Herzverlust. */
    fun onPairMismatch() {
        sounds.wrong()
        haptics.error()
    }

    /** Weiter zur nächsten Aufgabe. */
    fun next() {
        if (queue.isNotEmpty()) {
            queue.removeAt(0)
            if (answerState is AnswerState.Correct) solvedCount++
        }
        answerState = AnswerState.Waiting

        if (queue.isEmpty()) {
            if (repeatQueue.isNotEmpty()) {
                queue.addAll(repeatQueue)
                repeatQueue.clear()
            } else {
                complete()
            }
        }
    }

    private fun complete() {
        if (finished) return
        val baseXp = lesson?.xpReward ?: PRACTICE_XP
        earnedXp = ProgressLogic.lessonXp(baseXp, mistakes)
        finished = true
        viewModelScope.launch {
            val before = repository.progress.first()
            repository.completeLesson(
                // Freies Wiederholen zählt nicht als abgeschlossene Lektion.
                lessonId = lesson?.id,
                earnedXp = earnedXp,
                mistakes = mistakes,
                practicedItemIds = lesson?.itemIds ?: practicedIds
            )
            val after = repository.progress.first()
            newAchievements = LessonLogic.newlyUnlocked(
                Achievements.forProgress(before),
                Achievements.forProgress(after)
            )
            streakIncreased = after.streakDays > before.streakDays
            resultsReady = true
        }
    }

    companion object {
        const val PRACTICE_ID = "practice"
        private const val PRACTICE_XP = 10

        fun factory(
            repository: ProgressRepository,
            sounds: SoundPlayer,
            haptics: Haptics,
            lessonId: String,
            practiceItemIds: List<String>
        ) = viewModelFactory {
            initializer {
                LessonViewModel(repository, sounds, haptics, lessonId, practiceItemIds)
            }
        }
    }
}
```

- [ ] **Step 2: Kein eigener Commit** – `LessonScreen` ruft noch die alte Factory/`playWrongSound()` auf und kompiliert erst mit Task 5. Task 4 und 5 werden gemeinsam committet.

---

### Task 5: LessonScreen – Feedback-Leiste, Combo-Banner, Herz

**Files:**
- Replace: `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonScreen.kt`

**Interfaces:**
- Consumes: Task 3 (`ThickProgressBar(glow)`, `popIn`, `WordBankView(wrong)`), Task 4 (ViewModel), Task 6 Signatur von `LessonCompleteScreen` (unten fest definiert)
- Produces: nichts Neues

- [ ] **Step 1: Datei komplett ersetzen**

```kotlin
package de.turkischlernen.app.ui.lesson

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.model.Exercise
import de.turkischlernen.app.data.progress.LessonLogic
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.components.ThickProgressBar
import de.turkischlernen.app.ui.components.popIn
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val GLOW_MILLIS = 600L

/**
 * Der Lektions-Bildschirm: eine Aufgabe nach der anderen, unten die
 * Rückmeldung, oben Fortschritt, Combo und Herzen.
 */
@Composable
fun LessonScreen(
    lessonId: String,
    practiceItemIds: List<String>,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val progress by container.progressRepository.progress.collectAsState(initial = UserProgress())

    val viewModel: LessonViewModel = viewModel(
        factory = LessonViewModel.factory(
            repository = container.progressRepository,
            sounds = container.sounds,
            haptics = container.haptics,
            lessonId = lessonId,
            practiceItemIds = practiceItemIds
        )
    )

    var showExitDialog by remember { mutableStateOf(false) }

    val speak: (String, Boolean) -> Unit = { text, slow -> container.speech.speak(text, slow) }

    if (viewModel.finished) {
        LessonCompleteScreen(
            earnedXp = viewModel.earnedXp,
            accuracyPercent = viewModel.accuracyPercent,
            perfect = viewModel.mistakes == 0,
            streakDays = progress.streakDays,
            newAchievements = viewModel.newAchievements,
            streakIncreased = viewModel.streakIncreased,
            resultsReady = viewModel.resultsReady,
            onContinue = onExit
        )
        return
    }

    val exercise = viewModel.current
    if (exercise == null) {
        // Nichts zu üben (z. B. Wiederholen ohne gelernte Wörter).
        Column(
            modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🙂", fontSize = 60.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Hier gibt es gerade nichts zu wiederholen. Lerne zuerst ein paar neue Wörter!",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(20.dp))
            ChunkyButton("ZURÜCK", Modifier.fillMaxWidth()) { onExit() }
        }
        return
    }

    val interaction = remember(exercise) { ExerciseInteraction() }
    val locked = viewModel.answerState != AnswerState.Waiting
    val comboActive = viewModel.answerState == AnswerState.Correct &&
        LessonLogic.showsComboBanner(viewModel.combo)

    // Balken glüht kurz golden, wenn die Combo wächst.
    var glow by remember { mutableStateOf(false) }
    LaunchedEffect(comboActive, viewModel.combo) {
        if (comboActive) {
            glow = true
            delay(GLOW_MILLIS)
            glow = false
        }
    }

    Column(modifier.fillMaxSize()) {

        // Kopfzeile: Abbrechen, Fortschritt, Herzen
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "✕",
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { showExitDialog = true }
            )
            ThickProgressBar(
                fraction = viewModel.progressFraction,
                modifier = Modifier.weight(1f),
                glow = glow
            )
            HeartCounter(
                hearts = progress.hearts,
                unlimited = progress.unlimitedHearts,
                mistakes = viewModel.mistakes
            )
        }

        ComboBanner(combo = viewModel.combo, visible = comboActive)

        Text(
            text = exercise.prompt,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            when (exercise) {
                is Exercise.PictureChoice ->
                    PictureChoiceView(exercise, interaction, locked, speak)

                is Exercise.TranslateToGerman ->
                    TranslateView(exercise, interaction, locked, speak)

                is Exercise.Listening ->
                    ListeningView(exercise, interaction, locked, speak)

                is Exercise.WordBank ->
                    WordBankView(
                        exercise = exercise,
                        interaction = interaction,
                        locked = locked,
                        onSpeak = speak,
                        wrong = viewModel.answerState is AnswerState.Wrong
                    )

                is Exercise.MatchPairs ->
                    Column {
                        MatchPairsView(
                            exercise = exercise,
                            interaction = interaction,
                            onSpeak = speak,
                            onMismatch = { viewModel.onPairMismatch() }
                        )
                    }
            }
        }

        FeedbackBar(
            answerState = viewModel.answerState,
            praise = viewModel.praise,
            checkEnabled = isAnswerReady(exercise, interaction),
            onCheck = {
                val correct = isAnswerCorrect(exercise, interaction)
                viewModel.submitAnswer(
                    correct = correct,
                    correctAnswer = correctAnswerText(exercise),
                    itemIds = exercise.itemIds
                )
                speak(spokenText(exercise), false)
            },
            onContinue = { viewModel.next() },
            onReplay = { speak(spokenText(exercise), true) }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Lektion beenden?") },
            text = { Text("Dein Fortschritt in dieser Lektion geht verloren.") },
            confirmButton = {
                TextButton(onClick = onExit) { Text("Beenden") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Weiterlernen") }
            }
        )
    }

    if (!progress.hasHeartsLeft()) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Keine Herzen mehr ❤️") },
            text = {
                Text(
                    "Herzen wachsen von selbst wieder nach. " +
                        "Ihr könnt sie hier auch sofort auffüllen."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { container.progressRepository.refillHearts() }
                }) { Text("Auffüllen") }
            },
            dismissButton = {
                TextButton(onClick = onExit) { Text("Später weiter") }
            }
        )
    }
}

/** "🔥 N in Folge!" – der Platz bleibt reserviert, damit die Aufgabe nicht springt. */
@Composable
private fun ComboBanner(combo: Int, visible: Boolean) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(34.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔥 $combo in Folge!",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.Orange,
            modifier = Modifier.popIn(visible)
        )
    }
}

/** Herzanzeige: pulsiert bei jedem Fehler, die Zahl rollt weiter. */
@Composable
private fun HeartCounter(hearts: Int, unlimited: Boolean, mistakes: Int) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(mistakes) {
        if (mistakes > 0 && !unlimited) {
            scale.animateTo(1.4f, tween(120))
            scale.animateTo(0.8f, tween(120))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "❤️",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
        )
        Spacer(Modifier.width(4.dp))
        if (unlimited) {
            Text("∞", style = MaterialTheme.typography.titleMedium, color = AppColors.Red)
        } else {
            AnimatedContent(
                targetState = hearts,
                transitionSpec = {
                    (slideInVertically { -it } + fadeIn()) togetherWith
                        (slideOutVertically { it } + fadeOut())
                },
                label = "hearts"
            ) { count ->
                Text("$count", style = MaterialTheme.typography.titleMedium, color = AppColors.Red)
            }
        }
    }
}

/** Unterer Balken: "Prüfen" bzw. Rückmeldung und "Weiter" – fährt von unten ein. */
@Composable
private fun FeedbackBar(
    answerState: AnswerState,
    praise: String,
    checkEnabled: Boolean,
    onCheck: () -> Unit,
    onContinue: () -> Unit,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background by animateColorAsState(
        targetValue = when (answerState) {
            AnswerState.Waiting -> MaterialTheme.colorScheme.background
            AnswerState.Correct -> AppColors.GreenLight
            is AnswerState.Wrong -> AppColors.RedLight
        },
        label = "feedbackBackground"
    )

    Surface(color = background, modifier = modifier.fillMaxWidth()) {
        AnimatedContent(
            targetState = answerState,
            contentKey = { it::class },
            transitionSpec = {
                (slideInVertically { it } + fadeIn()) togetherWith fadeOut()
            },
            label = "feedback"
        ) { state ->
            Column(Modifier.padding(16.dp)) {
                when (state) {
                    AnswerState.Waiting -> Unit

                    AnswerState.Correct -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🎉", fontSize = 30.sp)
                        Text(
                            praise,
                            style = MaterialTheme.typography.titleLarge,
                            color = AppColors.GreenDark
                        )
                    }

                    is AnswerState.Wrong -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🙂", fontSize = 30.sp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Fast! Richtig ist:",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppColors.RedDark
                            )
                            Text(
                                state.correctAnswer,
                                style = MaterialTheme.typography.titleLarge,
                                color = AppColors.RedDark
                            )
                        }
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .clickable { onReplay() }
                                .padding(8.dp)
                        ) {
                            Text("🔊", fontSize = 22.sp)
                        }
                    }
                }

                if (state != AnswerState.Waiting) Spacer(Modifier.height(12.dp))

                if (state == AnswerState.Waiting) {
                    ChunkyButton(
                        text = "PRÜFEN",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = checkEnabled,
                        onClick = onCheck
                    )
                } else {
                    ChunkyButton(
                        text = "WEITER",
                        modifier = Modifier.fillMaxWidth(),
                        color = if (state is AnswerState.Wrong) AppColors.Red else AppColors.Green,
                        onClick = onContinue
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 2: Kein Commit** – kompiliert erst mit der neuen `LessonCompleteScreen`-Signatur aus Task 6.

---

### Task 6: LessonCompleteScreen – gestaffelter Abschluss

**Files:**
- Replace: `app/src/main/java/de/turkischlernen/app/ui/lesson/LessonCompleteScreen.kt`

**Interfaces:**
- Consumes: `popIn`, `AnimatedCounter` (Task 3), `LessonLogic.xpTickCount` (Task 1), `sounds.celebrate/xpTick/badge/streak` (Task 2), `Achievement`
- Produces: `LessonCompleteScreen(earnedXp: Int, accuracyPercent: Int, perfect: Boolean, streakDays: Int, newAchievements: List<Achievement>, streakIncreased: Boolean, resultsReady: Boolean, onContinue: () -> Unit, modifier: Modifier = Modifier)`

- [ ] **Step 1: Datei komplett ersetzen**

```kotlin
package de.turkischlernen.app.ui.lesson

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.progress.Achievement
import de.turkischlernen.app.data.progress.LessonLogic
import de.turkischlernen.app.ui.components.AnimatedCounter
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.components.ConfettiOverlay
import de.turkischlernen.app.ui.components.popIn
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.delay

private const val STAGE_TROPHY = 1
private const val STAGE_TITLE = 2
private const val STAGE_XP = 3
private const val STAGE_ACCURACY = 4
private const val STAGE_STREAK = 5
private const val STAGE_DONE = 6

private const val COUNTER_MILLIS = 900

/** Einblendungen nach dem Abschluss, nacheinander per Tippen. */
private sealed interface CelebrationOverlay {
    data class Badge(val achievement: Achievement) : CelebrationOverlay
    data class Streak(val days: Int) : CelebrationOverlay
}

/**
 * Abschlussbildschirm als kleiner Ablauf: Pokal, Titel, Karten nacheinander
 * (XP zählen hoch), danach neue Abzeichen und "Serie verlängert".
 */
@Composable
fun LessonCompleteScreen(
    earnedXp: Int,
    accuracyPercent: Int,
    perfect: Boolean,
    streakDays: Int,
    newAchievements: List<Achievement>,
    streakIncreased: Boolean,
    resultsReady: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sounds = LocalAppContainer.current.sounds

    var stage by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        sounds.celebrate()
        stage = STAGE_TROPHY
        delay(350)
        stage = STAGE_TITLE
        delay(300)
        stage = STAGE_XP
        delay(150)
        stage = STAGE_ACCURACY
        delay(150)
        stage = STAGE_STREAK
        delay(COUNTER_MILLIS.toLong())
        stage = STAGE_DONE
    }

    val overlays = remember(resultsReady, newAchievements, streakIncreased, streakDays) {
        if (!resultsReady) {
            emptyList()
        } else {
            buildList {
                newAchievements.forEach { add(CelebrationOverlay.Badge(it)) }
                if (streakIncreased) add(CelebrationOverlay.Streak(streakDays))
            }
        }
    }
    var overlayIndex by remember { mutableIntStateOf(0) }
    val activeOverlay = if (stage >= STAGE_DONE) overlays.getOrNull(overlayIndex) else null

    LaunchedEffect(activeOverlay) {
        when (activeOverlay) {
            is CelebrationOverlay.Badge -> sounds.badge()
            is CelebrationOverlay.Streak -> sounds.streak()
            null -> Unit
        }
    }

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                if (perfect) "🏆" else "🎉",
                fontSize = 96.sp,
                modifier = Modifier.popIn(stage >= STAGE_TROPHY)
            )
            Spacer(Modifier.height(12.dp))
            Column(
                Modifier.popIn(stage >= STAGE_TITLE),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (perfect) "Perfekt!" else "Geschafft!",
                    style = MaterialTheme.typography.displaySmall,
                    color = AppColors.GreenDark,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (perfect) {
                        "Alles richtig – du bist ein Türkisch-Profi! 🌟"
                    } else {
                        "Weiter so! Übung macht den Meister."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(28.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultCard(
                    emoji = "⭐", label = "gesammelt", color = AppColors.Gold,
                    modifier = Modifier
                        .weight(1f)
                        .popIn(stage >= STAGE_XP)
                ) {
                    AnimatedCounter(
                        target = earnedXp,
                        start = stage >= STAGE_XP,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.Gold,
                        suffix = " XP",
                        durationMillis = COUNTER_MILLIS,
                        ticks = LessonLogic.xpTickCount(earnedXp),
                        onTick = { sounds.xpTick() }
                    )
                }
                ResultCard(
                    emoji = "🎯", label = "Genauigkeit", color = AppColors.Blue,
                    modifier = Modifier
                        .weight(1f)
                        .popIn(stage >= STAGE_ACCURACY)
                ) {
                    Text("$accuracyPercent %", style = MaterialTheme.typography.titleLarge, color = AppColors.Blue)
                }
                ResultCard(
                    emoji = "🔥", label = if (streakDays == 1) "Tag Serie" else "Tage Serie",
                    color = AppColors.Orange,
                    modifier = Modifier
                        .weight(1f)
                        .popIn(stage >= STAGE_STREAK)
                ) {
                    Text("$streakDays", style = MaterialTheme.typography.titleLarge, color = AppColors.Orange)
                }
            }

            Spacer(Modifier.height(36.dp))
            ChunkyButton(
                text = "WEITER",
                modifier = Modifier
                    .fillMaxWidth()
                    .popIn(stage >= STAGE_DONE),
                enabled = stage >= STAGE_DONE
            ) { onContinue() }
        }

        ConfettiOverlay()

        if (activeOverlay != null) {
            key(overlayIndex) {
                CelebrationOverlayView(activeOverlay) { overlayIndex++ }
            }
        }
    }
}

@Composable
private fun ResultCard(
    emoji: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, color, RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 26.sp)
        value()
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Vollbild-Einblendung für ein neues Abzeichen oder die verlängerte Serie. */
@Composable
private fun CelebrationOverlayView(overlay: CelebrationOverlay, onDismiss: () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val pulseModifier = Modifier.graphicsLayer {
        scaleX = pulse
        scaleY = pulse
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(32.dp)
                .popIn(shown)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (overlay) {
                is CelebrationOverlay.Badge -> {
                    Text("Neues Abzeichen!", style = MaterialTheme.typography.titleMedium, color = AppColors.Gold)
                    Spacer(Modifier.height(8.dp))
                    Text(overlay.achievement.emoji, fontSize = 80.sp, modifier = pulseModifier)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        overlay.achievement.title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        overlay.achievement.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                is CelebrationOverlay.Streak -> {
                    Text("🔥", fontSize = 96.sp, modifier = pulseModifier)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Serie verlängert!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppColors.Orange
                    )
                    Text(
                        if (overlay.days == 1) "1 Tag in Folge" else "${overlay.days} Tage in Folge",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Tippe, um weiterzumachen",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

- [ ] **Step 2: Tasks 4–6 gemeinsam committen + CI prüfen – erwartet PASS**

```bash
git add app/src/main/java/de/turkischlernen/app/ui/lesson
git commit -m "Lektion: Combo-Serie, Lobsprüche, animierte Rückmeldung und gestaffelter Abschluss"
```

---

### Task 7: Schalter im Eltern-Bereich

**Files:**
- Modify: `app/src/main/java/de/turkischlernen/app/ui/profile/ProfileScreen.kt`

**Interfaces:**
- Consumes: `container.settingsRepository.current`, `setSoundEnabled`, `setHapticsEnabled` (Task 2)

- [ ] **Step 1: Import ergänzen**

```kotlin
import androidx.compose.runtime.collectAsState
```

- [ ] **Step 2: Einstellungen lesen** – direkt nach `var showParentArea by remember { mutableStateOf(false) }` einfügen:

```kotlin
    val settings by container.settingsRepository.current.collectAsState()
```

- [ ] **Step 3: Herzen-Zeile ersetzen** – den kompletten `Row(...) { Column(Modifier.weight(1f)) { Text("Unbegrenzte Herzen" ... } Switch(...) }` (Zeilen 190–211) ersetzen durch:

```kotlin
                    SettingSwitch(
                        title = "Unbegrenzte Herzen",
                        subtitle = "Aus: Bei Fehlern gehen Herzen verloren (wie in Duolingo).",
                        checked = progress.unlimitedHearts,
                        onCheckedChange = { enabled ->
                            scope.launch { container.progressRepository.setUnlimitedHearts(enabled) }
                        }
                    )

                    SettingSwitch(
                        title = "Sounds",
                        subtitle = "Töne bei Antworten und Belohnungen. Die Aussprache bleibt an.",
                        checked = settings.soundEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch { container.settingsRepository.setSoundEnabled(enabled) }
                        }
                    )

                    SettingSwitch(
                        title = "Vibration",
                        subtitle = "Kurze Vibration bei richtigen und falschen Antworten.",
                        checked = settings.hapticsEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch { container.settingsRepository.setHapticsEnabled(enabled) }
                        }
                    )
```

- [ ] **Step 4: Hilfsfunktion am Dateiende** (nach `GoalChip`) ergänzen:

```kotlin
/** Einstellungszeile mit Titel, Erklärung und Schalter. */
@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
```

- [ ] **Step 5: Commit + CI prüfen – erwartet PASS**

```bash
git add app/src/main/java/de/turkischlernen/app/ui/profile/ProfileScreen.kt
git commit -m "Eltern-Bereich: Schalter für Sounds und Vibration"
```

---

### Task 8: APK aufs Handy, manueller Test, PR

- [ ] **Step 1: APK des letzten grünen CI-Laufs laden**

```bash
gh run download <RUN_ID> -R SaitTan/TurkischLernApp -n tuerkisch-lernen-apk -D "<SCRATCHPAD>/apk-etappe1"
```

- [ ] **Step 2: Installieren** (Handy per USB, Debugging erlaubt)

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r "<SCRATCHPAD>\apk-etappe1\release\app-release.apk"
```

Expected: `Success`. Fortschritt bleibt erhalten (gleiche Signatur, `-r`).

- [ ] **Step 3: Manuelle Prüfliste mit dem Nutzer**
  - Antwort antippen: Karte gibt nach, Klick-Sound
  - Richtig: Ton, Vibration, Leiste fährt ein, Karte hüpft, wechselnde Lobsprüche
  - Falsch: Ton, Doppel-Vibration, Karte wackelt, Lösung sichtbar
  - Satz bauen falsch/richtig: Antwortzeile wackelt/hüpft
  - Paare finden falsches Paar: Wackeln + Ton, kein Herzverlust
  - 3+ richtig in Folge: Banner „🔥 N in Folge!“, Ton wird höher, Balken glüht
  - Herzen begrenzt (Eltern-Bereich) + Fehler: Herz pulsiert, Zahl rollt
  - Abschluss: Pokal → Titel → Karten nacheinander, XP zählen mit Ticks, Genauigkeit in %
  - Neues Abzeichen / Serie verlängert: Einblendung, Tippen schließt
  - Schalter Sounds/Vibration aus → still bzw. keine Vibration; Aussprache bleibt
  - Sounds gefallen? Sonst Datei in `res/raw` tauschen (Tabelle in `LICENSES.md` anpassen)

- [ ] **Step 4: PR erstellen**

```bash
gh pr create -R SaitTan/TurkischLernApp --base main --head claude/lektions-gefuehl --title "Etappe 1: Lektions-Gefühl (Sounds, Vibration, Animationen, Abschluss)" --body-file <PR_BODY_FILE>
```

PR-Text: Zusammenfassung der Tasks, Hinweis auf Spec/Plan, Prüfliste aus Step 3, endet mit
`🤖 Generated with [Claude Code](https://claude.com/claude-code)`.
