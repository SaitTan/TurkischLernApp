package de.turkischlernen.app.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/**
 * Hört zu und erkennt gesprochenes Türkisch. Jeder Fehler – keine Sprachdaten,
 * kein Netz, nichts verstanden – meldet schlicht `null`; die Lektion schaltet
 * dann auf Nachsprechen um.
 */
class SpeechListener(context: Context) {

    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null

    /** Gibt es auf diesem Gerät überhaupt eine Spracherkennung? */
    val available: Boolean
        get() = runCatching { SpeechRecognizer.isRecognitionAvailable(appContext) }.getOrDefault(false)

    /**
     * Startet die Aufnahme. [onResult] bekommt den erkannten Text oder `null`.
     * Muss vom Haupt-Thread aufgerufen werden.
     */
    fun start(onResult: (String?) -> Unit) {
        stop()
        if (!available) {
            onResult(null)
            return
        }

        val erkenner = runCatching { SpeechRecognizer.createSpeechRecognizer(appContext) }.getOrNull()
        if (erkenner == null) {
            onResult(null)
            return
        }
        recognizer = erkenner

        erkenner.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val text = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                onResult(text)
                stop()
            }

            override fun onError(error: Int) {
                onResult(null)
                stop()
            }

            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })

        val absicht = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
        }

        runCatching { erkenner.startListening(absicht) }.onFailure {
            onResult(null)
            stop()
        }
    }

    fun stop() {
        runCatching {
            recognizer?.cancel()
            recognizer?.destroy()
        }
        recognizer = null
    }
}
