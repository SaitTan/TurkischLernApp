package de.turkischlernen.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import de.turkischlernen.app.R
import de.turkischlernen.app.data.settings.SoundKind
import java.util.concurrent.ConcurrentHashMap

/**
 * Feedback-Sounds (CC0 von Kenney, siehe LICENSES.md). Lädt alle Sounds beim
 * Start vor, damit sie ohne Verzögerung klingen. Nicht geladene Sounds bleiben still.
 */
class SoundPlayer(context: Context, private val allowed: (SoundKind) -> Boolean) {

    private enum class Sound(@RawRes val res: Int) {
        TAP(R.raw.sfx_tap),
        CORRECT(R.raw.sfx_correct),
        WRONG(R.raw.sfx_wrong),
        KANGAL_CORRECT(R.raw.sfx_kangal_correct),
        KANGAL_WRONG(R.raw.sfx_kangal_wrong),
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

    fun tap() = play(Sound.TAP, SoundKind.TAP, volume = 0.5f)
    fun correct() = play(Sound.CORRECT, SoundKind.ANSWER)
    fun wrong() = play(Sound.WRONG, SoundKind.ANSWER)

    /** Stimme des Maskottchens – kommt direkt nach dem Prüfungs-Ton. */
    fun kangalCorrect() = play(Sound.KANGAL_CORRECT, SoundKind.VOICE)

    fun kangalWrong() = play(Sound.KANGAL_WRONG, SoundKind.VOICE)

    /** Combo-Ton – je höher die Stufe (1..5), desto höher der Klang. */
    fun combo(level: Int) =
        play(Sound.COMBO, SoundKind.COMBO, rate = 1f + 0.1f * (level - 1).coerceIn(0, 4))

    fun xpTick() = play(Sound.XP_TICK, SoundKind.XP, volume = 0.6f)
    fun celebrate() = play(Sound.CELEBRATE, SoundKind.REWARD)
    fun badge() = play(Sound.BADGE, SoundKind.REWARD)
    fun streak() = play(Sound.STREAK, SoundKind.REWARD)

    private fun play(sound: Sound, kind: SoundKind, volume: Float = 1f, rate: Float = 1f) {
        if (!allowed(kind)) return
        val id = sampleIds[sound] ?: return
        if (id !in loaded) return
        runCatching { pool.play(id, volume, volume, 1, 0, rate) }
    }

    fun release() {
        pool.release()
    }
}
