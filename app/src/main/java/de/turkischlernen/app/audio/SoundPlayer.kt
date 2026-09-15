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
