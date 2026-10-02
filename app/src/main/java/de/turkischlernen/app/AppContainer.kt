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
