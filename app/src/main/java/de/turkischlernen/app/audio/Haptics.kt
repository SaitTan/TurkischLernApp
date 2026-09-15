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
