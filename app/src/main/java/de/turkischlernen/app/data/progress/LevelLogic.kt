package de.turkischlernen.app.data.progress

/** Aus XP wird ein Level: je [XP_PER_LEVEL] Punkte eine Stufe. */
object LevelLogic {

    const val XP_PER_LEVEL = 100

    /** Extra-Spielzeit beim Aufstieg. */
    const val LEVEL_UP_PLAY_MINUTES = 5

    fun levelOf(totalXp: Int): Int = totalXp.coerceAtLeast(0) / XP_PER_LEVEL + 1

    /** Bereits gesammelte XP innerhalb der aktuellen Stufe. */
    fun xpIntoLevel(totalXp: Int): Int = totalXp.coerceAtLeast(0) % XP_PER_LEVEL

    /** Noch fehlende XP bis zur nächsten Stufe. */
    fun xpToNext(totalXp: Int): Int = XP_PER_LEVEL - xpIntoLevel(totalXp)

    fun leveledUp(before: Int, after: Int): Boolean = levelOf(after) > levelOf(before)

    /** Jede Stufe hat ihre eigene Truhe, damit es sie nur einmal gibt. */
    fun chestId(level: Int): String = "chest_level_$level"
}
