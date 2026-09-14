package de.turkischlernen.app.data.progress

import de.turkischlernen.app.data.content.Curriculum

data class Achievement(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val unlocked: Boolean
)

/** Abzeichen, die das Kind sammeln kann – rein aus dem Fortschritt berechnet. */
object Achievements {

    fun forProgress(progress: UserProgress): List<Achievement> {
        val completedUnits = Curriculum.units.count { unit ->
            unit.lessons.all { it.id in progress.completedLessons }
        }
        return listOf(
            Achievement(
                "first_lesson", "🌱", "Erster Schritt",
                "Die erste Lektion geschafft",
                progress.lessonRuns >= 1
            ),
            Achievement(
                "ten_words", "🔤", "10 Wörter",
                "10 türkische Wörter gelernt",
                progress.learnedItems.size >= 10
            ),
            Achievement(
                "twentyfive_words", "📚", "25 Wörter",
                "25 türkische Wörter gelernt",
                progress.learnedItems.size >= 25
            ),
            Achievement(
                "fifty_words", "🧠", "50 Wörter",
                "50 türkische Wörter gelernt",
                progress.learnedItems.size >= 50
            ),
            Achievement(
                "perfect", "🎯", "Fehlerfrei",
                "Eine Lektion ohne Fehler",
                progress.perfectLessons.isNotEmpty()
            ),
            Achievement(
                "streak3", "🔥", "3 Tage Serie",
                "3 Tage hintereinander gelernt",
                progress.streakDays >= 3
            ),
            Achievement(
                "streak7", "⚡", "7 Tage Serie",
                "Eine ganze Woche gelernt",
                progress.streakDays >= 7
            ),
            Achievement(
                "xp100", "⭐", "100 XP",
                "100 Erfahrungspunkte gesammelt",
                progress.totalXp >= 100
            ),
            Achievement(
                "xp500", "🌟", "500 XP",
                "500 Erfahrungspunkte gesammelt",
                progress.totalXp >= 500
            ),
            Achievement(
                "unit", "🏆", "Einheit geschafft",
                "Eine komplette Einheit abgeschlossen",
                completedUnits >= 1
            )
        )
    }
}
