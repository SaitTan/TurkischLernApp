package de.turkischlernen.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Kurze Feedback-Töne (richtig / falsch / Lektion geschafft).
 *
 * Die Töne werden zur Laufzeit berechnet – dadurch braucht die App keine
 * Audiodateien und bleibt klein und vollständig offline.
 */
class SoundPlayer {

    private val executor = Executors.newSingleThreadExecutor()

    /** Fröhlicher Dreiklang bei einer richtigen Antwort. */
    fun correct() = play(listOf(Tone(660f, 90), Tone(880f, 90), Tone(1320f, 200)))

    /** Weicher, tiefer Ton bei einer falschen Antwort – bewusst nicht "böse". */
    fun wrong() = play(listOf(Tone(330f, 120), Tone(247f, 220)))

    /** Kleine Fanfare am Ende einer Lektion. */
    fun celebrate() = play(
        listOf(
            Tone(523f, 110), Tone(659f, 110), Tone(784f, 110),
            Tone(1047f, 320)
        )
    )

    /** Leises Tippgeräusch. */
    fun tap() = play(listOf(Tone(880f, 45, volume = 0.25f)))

    private data class Tone(val frequencyHz: Float, val durationMs: Int, val volume: Float = 0.6f)

    private fun play(tones: List<Tone>) {
        executor.execute {
            runCatching {
                val samples = render(tones)
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()
                track.write(samples, 0, samples.size)
                track.stop()
                track.release()
            }
        }
    }

    private fun render(tones: List<Tone>): ShortArray {
        val total = tones.sumOf { (SAMPLE_RATE * it.durationMs / 1000.0).toInt() }
        val out = ShortArray(total)
        var offset = 0
        tones.forEach { tone ->
            val count = (SAMPLE_RATE * tone.durationMs / 1000.0).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                // Weiche Hüllkurve, damit es nicht knackt.
                val attack = (i / (SAMPLE_RATE * 0.01)).coerceAtMost(1.0)
                val decay = exp(-3.0 * i / count)
                val value = sin(2.0 * PI * tone.frequencyHz * t) * attack * decay * tone.volume
                out[offset + i] = (value * Short.MAX_VALUE).toInt().toShort()
            }
            offset += count
        }
        return out
    }

    fun release() {
        executor.shutdown()
    }

    private companion object {
        const val SAMPLE_RATE = 22050
    }
}
