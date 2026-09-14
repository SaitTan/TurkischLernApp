package de.turkischlernen.app

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import de.turkischlernen.app.audio.SoundPlayer
import de.turkischlernen.app.audio.SpeechManager
import de.turkischlernen.app.data.progress.ProgressRepository

/**
 * Einfache, manuelle Dependency-Verwaltung (kein DI-Framework nötig).
 * Lebt so lange wie der Prozess.
 */
class AppContainer(context: Context) {
    val progressRepository: ProgressRepository = ProgressRepository(context)
    val speech: SpeechManager = SpeechManager(context)
    val sounds: SoundPlayer = SoundPlayer()

    fun release() {
        speech.shutdown()
        sounds.release()
    }
}

val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("AppContainer wurde nicht bereitgestellt")
}
