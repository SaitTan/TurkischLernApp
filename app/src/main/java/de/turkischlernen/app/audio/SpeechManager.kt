package de.turkischlernen.app.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/**
 * Spricht türkische Wörter und Sätze über die Android Text-to-Speech Engine
 * (Locale tr-TR). Läuft komplett offline, sofern die türkischen Sprachdaten
 * auf dem Gerät installiert sind.
 */
class SpeechManager(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null

    /** true, sobald die Engine bereit ist. */
    var ready by mutableStateOf(false)
        private set

    /** false, wenn Türkisch auf dem Gerät fehlt – die App zeigt dann einen Hinweis. */
    var turkishAvailable by mutableStateOf(true)
        private set

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val engine = tts
                val result = engine?.setLanguage(TURKISH)
                turkishAvailable = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                engine?.setPitch(1.05f)
                engine?.setSpeechRate(NORMAL_RATE)
                ready = true
            } else {
                ready = false
                turkishAvailable = false
            }
        }
    }

    /**
     * Liest [text] auf Türkisch vor.
     * @param slow langsamere, deutlichere Aussprache (z. B. bei langem Antippen)
     */
    fun speak(text: String, slow: Boolean = false) {
        val engine = tts ?: return
        if (!ready || text.isBlank()) return
        engine.setSpeechRate(if (slow) SLOW_RATE else NORMAL_RATE)
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }

    companion object {
        private val TURKISH: Locale = Locale.forLanguageTag("tr-TR")
        private const val NORMAL_RATE = 0.9f
        private const val SLOW_RATE = 0.55f
    }
}
