package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.progress.UserProgress
import java.time.LocalDate
import kotlin.random.Random

/** Worauf sich eine Tagesaufgabe bezieht. */
enum class QuestKind { LESSONS, XP, PERFECT, WORDS, CORRECT }

/** Eine Tagesaufgabe mit Ziel, Text und Symbol. */
data class Quest(
    val id: String,
    val kind: QuestKind,
    val goal: Int,
    val label: String,
    val emoji: String
)

/**
 * Die drei Aufgaben des Tages. Gewürfelt wird mit dem Datum als Startwert –
 * derselbe Tag ergibt also immer dieselben Aufgaben, auch nach einem Neustart.
 */
object DailyQuests {

    const val COUNT = 3

    val pool = listOf(
        Quest("lektion1", QuestKind.LESSONS, 1, "Schaffe 1 Lektion", "✅"),
        Quest("lektion2", QuestKind.LESSONS, 2, "Schaffe 2 Lektionen", "🏁"),
        Quest("xp20", QuestKind.XP, 20, "Sammle 20 XP", "⭐"),
        Quest("xp40", QuestKind.XP, 40, "Sammle 40 XP", "🌟"),
        Quest("perfekt1", QuestKind.PERFECT, 1, "Beende eine Runde ohne Fehler", "🎯"),
        Quest("woerter10", QuestKind.WORDS, 10, "Übe 10 Wörter", "🔤"),
        Quest("richtig15", QuestKind.CORRECT, 15, "Antworte 15-mal richtig", "👍")
    )

    /** Drei verschiedene Aufgaben für [date]. */
    fun forDay(date: LocalDate): List<Quest> =
        pool.shuffled(Random(date.toEpochDay())).take(COUNT)

    fun progressOf(quest: Quest, progress: UserProgress): Int = when (quest.kind) {
        QuestKind.LESSONS -> progress.lessonsToday
        QuestKind.XP -> progress.xpToday
        QuestKind.PERFECT -> progress.perfectToday
        QuestKind.WORDS -> progress.wordsToday
        QuestKind.CORRECT -> progress.correctToday
    }

    fun isDone(quest: Quest, progress: UserProgress): Boolean =
        progressOf(quest, progress) >= quest.goal

    /** Alle drei Aufgaben erledigt? Dann gibt es die Tages-Truhe. */
    fun allDone(date: LocalDate, progress: UserProgress): Boolean =
        forDay(date).all { isDone(it, progress) }
}
