package de.turkischlernen.app.data.progress

import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Rechenlogik rund um eine laufende Lektion – ohne Android-Abhängigkeiten,
 * damit sie in Unit-Tests geprüft werden kann.
 */
object LessonLogic {

    /** Ab so vielen richtigen Antworten in Folge erscheint das Combo-Banner. */
    const val COMBO_THRESHOLD = 3

    private const val MAX_COMBO_LEVEL = 5
    private const val MAX_XP_TICKS = 10

    val PRAISES = listOf("Super!", "Stark!", "Klasse!", "Richtig!", "Harika!", "Aferin!", "Çok iyi!")

    fun nextCombo(current: Int, correct: Boolean): Int = if (correct) current + 1 else 0

    fun showsComboBanner(combo: Int): Boolean = combo >= COMBO_THRESHOLD

    /** 0 unterhalb der Schwelle, danach 1 bis 5 – steuert die Tonhöhe. */
    fun comboLevel(combo: Int): Int =
        if (combo < COMBO_THRESHOLD) 0 else (combo - COMBO_THRESHOLD + 1).coerceAtMost(MAX_COMBO_LEVEL)

    fun accuracyPercent(correct: Int, total: Int): Int =
        if (total == 0) 100 else (correct * 100.0 / total).roundToInt()

    /** Abzeichen, die in [after] freigeschaltet sind, in [before] aber noch nicht. */
    fun newlyUnlocked(before: List<Achievement>, after: List<Achievement>): List<Achievement> {
        val unlockedBefore = before.filter { it.unlocked }.map { it.id }.toSet()
        return after.filter { it.unlocked && it.id !in unlockedBefore }
    }

    /** Zufälliger Lobspruch, nie derselbe wie [previous]. */
    fun pickPraise(previous: String?, random: Random): String {
        val candidates = PRAISES.filter { it != previous }
        return candidates[random.nextInt(candidates.size)]
    }

    /** Anzahl der Tick-Sounds beim Hochzählen der XP. */
    fun xpTickCount(xp: Int): Int = xp.coerceIn(0, MAX_XP_TICKS)
}
