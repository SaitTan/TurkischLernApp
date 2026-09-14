package de.turkischlernen.app.data.progress

import java.time.LocalDate

/** Der komplette Lernfortschritt des Kindes (lokal gespeichert, keine Cloud). */
data class UserProgress(
    val totalXp: Int = 0,
    val xpToday: Int = 0,
    val dailyGoal: Int = 30,
    val streakDays: Int = 0,
    val lastActiveDate: String = "",
    val hearts: Int = MAX_HEARTS,
    val heartsUpdatedAt: Long = 0L,
    val unlimitedHearts: Boolean = true,
    val completedLessons: Set<String> = emptySet(),
    val perfectLessons: Set<String> = emptySet(),
    val learnedItems: Set<String> = emptySet(),
    val mistakeItems: Set<String> = emptySet(),
    val lessonRuns: Int = 0
) {
    val dailyGoalReached: Boolean get() = xpToday >= dailyGoal

    fun isLessonDone(lessonId: String): Boolean = lessonId in completedLessons

    fun hasHeartsLeft(): Boolean = unlimitedHearts || hearts > 0

    companion object {
        const val MAX_HEARTS = 5

        /** Ein Herz wächst alle 20 Minuten nach. */
        const val HEART_REFILL_MILLIS = 20L * 60L * 1000L
    }
}

/**
 * Reine Rechenlogik rund um den Fortschritt – bewusst ohne Android-Abhängig-
 * keiten, damit sie in Unit-Tests geprüft werden kann.
 */
object ProgressLogic {

    /** Herzen, die seit [UserProgress.heartsUpdatedAt] nachgewachsen sind. */
    fun regeneratedHearts(progress: UserProgress, nowMillis: Long): Int {
        if (progress.hearts >= UserProgress.MAX_HEARTS) return UserProgress.MAX_HEARTS
        if (progress.heartsUpdatedAt <= 0L) return progress.hearts
        val elapsed = nowMillis - progress.heartsUpdatedAt
        if (elapsed <= 0L) return progress.hearts
        val gained = (elapsed / UserProgress.HEART_REFILL_MILLIS).toInt()
        return (progress.hearts + gained).coerceAtMost(UserProgress.MAX_HEARTS)
    }

    /**
     * Neuer Streak-Stand nach einer abgeschlossenen Lektion.
     * Gleicher Tag → unverändert, gestern → +1, sonst → Neustart bei 1.
     */
    fun nextStreak(lastActiveDate: String, streakDays: Int, today: LocalDate): Int = when (lastActiveDate) {
        today.toString() -> if (streakDays == 0) 1 else streakDays
        today.minusDays(1).toString() -> streakDays + 1
        else -> 1
    }

    /** Tages-XP: am neuen Tag wird bei 0 begonnen. */
    fun nextXpToday(lastActiveDate: String, xpToday: Int, earned: Int, today: LocalDate): Int =
        if (lastActiveDate == today.toString()) xpToday + earned else earned

    /** XP einer Lektion inkl. Bonus für eine fehlerfreie Runde. */
    fun lessonXp(baseXp: Int, mistakes: Int): Int = if (mistakes == 0) baseXp + 5 else baseXp
}
