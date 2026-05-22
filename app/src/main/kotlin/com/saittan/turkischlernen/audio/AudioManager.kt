package com.saittan.turkischlernen.audio

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RawRes
import java.util.Locale

/**
 * Spielt Audio ab. Bevorzugt vorgefertigte MP3/OGG-Dateien aus res/raw;
 * fällt auf Android TextToSpeech mit Locale tr_TR zurück.
 */
class AudioManager(context: Context) {

    private val appContext = context.applicationContext
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var turkishAvailable = false

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("tr", "TR"))
                turkishAvailable = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                ttsReady = true
            } else {
                Log.w(TAG, "TTS init failed: $status")
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

    fun isTurkishVoiceAvailable(): Boolean = ttsReady && turkishAvailable

    private fun speakTts(text: String) {
        val engine = tts ?: return
        if (!ttsReady) return
        engine.stop()
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
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
