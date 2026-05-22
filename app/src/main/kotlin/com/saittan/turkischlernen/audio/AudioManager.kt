package com.saittan.turkischlernen.audio

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RawRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class TtsStatus { Initializing, Ready, TurkishMissing }

/**
 * Spielt Audio ab. Bevorzugt vorgefertigte MP3/OGG-Dateien aus res/raw;
 * fällt auf Android TextToSpeech mit Locale tr_TR zurück.
 */
class AudioManager(context: Context) {

    private val appContext = context.applicationContext
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val _status = MutableStateFlow(TtsStatus.Initializing)
    val status: StateFlow<TtsStatus> = _status.asStateFlow()

    init {
        Log.d(TAG, "TTS: starting init…")
        tts = TextToSpeech(appContext) { initStatus ->
            Log.d(TAG, "TTS init callback: status=$initStatus (SUCCESS=${TextToSpeech.SUCCESS})")
            if (initStatus == TextToSpeech.SUCCESS) {
                val engine = tts
                val available = engine?.availableLanguages?.joinToString { "${it.language}_${it.country}" }
                Log.d(TAG, "TTS engine default: ${engine?.defaultEngine}, available locales: $available")
                val result = engine?.setLanguage(Locale("tr", "TR"))
                Log.d(TAG, "TTS setLanguage(tr_TR) returned: $result " +
                    "(MISSING=${TextToSpeech.LANG_MISSING_DATA}, NOT_SUPPORTED=${TextToSpeech.LANG_NOT_SUPPORTED}, " +
                    "AVAILABLE=${TextToSpeech.LANG_AVAILABLE})")
                val turkishOk = result != null &&
                    result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                ttsReady = true
                _status.value = if (turkishOk) TtsStatus.Ready else TtsStatus.TurkishMissing
                Log.d(TAG, "TTS final status: ${_status.value}")
            } else {
                Log.w(TAG, "TTS init failed (no engine?): $initStatus")
                _status.value = TtsStatus.TurkishMissing
            }
        }
    }

    /**
     * Spielt das Wort/den Satz ab. Wenn [audioResId] angegeben ist, wird die
     * Audiodatei verwendet, sonst Text-to-Speech.
     */
    fun speak(text: String, @RawRes audioResId: Int? = null) {
        if (audioResId != null) {
            playRaw(audioResId)
            return
        }
        speakTts(text)
    }

    private fun speakTts(text: String) {
        val engine = tts
        if (engine == null) {
            Log.w(TAG, "speak('$text'): TTS engine is null")
            return
        }
        if (!ttsReady) {
            Log.w(TAG, "speak('$text'): TTS not ready yet (status=${_status.value})")
            return
        }
        engine.stop()
        val result = engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
        Log.d(TAG, "speak('$text') returned $result (SUCCESS=${TextToSpeech.SUCCESS})")
    }

    private fun playRaw(@RawRes audioResId: Int) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(appContext, audioResId)?.apply {
                setOnCompletionListener { mp ->
                    mp.release()
                    if (mediaPlayer === mp) mediaPlayer = null
                }
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "playRaw failed", e)
        }
    }

    fun shutdown() {
        mediaPlayer?.release()
        mediaPlayer = null
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    companion object {
        private const val TAG = "AudioManager"
    }
}
