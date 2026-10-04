package de.turkischlernen.app

import de.turkischlernen.app.data.content.DailyQuests
import de.turkischlernen.app.data.content.QuestKind
import de.turkischlernen.app.data.progress.UserProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DailyQuestsTest {

    private val tag: LocalDate = LocalDate.of(2026, 10, 2)

    @Test
    fun `derselbe Tag ergibt immer dieselben drei Aufgaben`() {
        val a = DailyQuests.forDay(tag).map { it.id }
        val b = DailyQuests.forDay(tag).map { it.id }
        assertEquals(a, b)
        assertEquals(3, a.size)
    }

    @Test
    fun `die drei Aufgaben sind verschieden`() {
        val ids = DailyQuests.forDay(tag).map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `jede Aufgabe hat Text, Emoji und ein Ziel groesser null`() {
        DailyQuests.pool.forEach { quest ->
            assertTrue("Text fehlt bei ${quest.id}", quest.label.isNotBlank())
            assertTrue("Emoji fehlt bei ${quest.id}", quest.emoji.isNotBlank())
            assertTrue("Ziel ist null bei ${quest.id}", quest.goal > 0)
        }
    }

    @Test
    fun `der Fortschritt wird aus den Tageszaehlern gelesen`() {
        val progress = UserProgress(
            xpToday = 12,
            lessonsToday = 1,
            perfectToday = 2,
            wordsToday = 7,
            correctToday = 9,
            reviewedToday = 4
        )
        assertEquals(1, DailyQuests.progressOf(DailyQuests.pool.first { it.kind == QuestKind.LESSONS }, progress))
        assertEquals(12, DailyQuests.progressOf(DailyQuests.pool.first { it.kind == QuestKind.XP }, progress))
        assertEquals(2, DailyQuests.progressOf(DailyQuests.pool.first { it.kind == QuestKind.PERFECT }, progress))
        assertEquals(7, DailyQuests.progressOf(DailyQuests.pool.first { it.kind == QuestKind.WORDS }, progress))
        assertEquals(9, DailyQuests.progressOf(DailyQuests.pool.first { it.kind == QuestKind.CORRECT }, progress))
        assertEquals(4, DailyQuests.progressOf(DailyQuests.pool.first { it.kind == QuestKind.REVIEW }, progress))
    }

    @Test
    fun `es gibt eine Aufgabe zum Auffrischen`() {
        val quest = DailyQuests.pool.first { it.kind == QuestKind.REVIEW }
        assertFalse(DailyQuests.isDone(quest, UserProgress(reviewedToday = quest.goal - 1)))
        assertTrue(DailyQuests.isDone(quest, UserProgress(reviewedToday = quest.goal)))
    }

    @Test
    fun `eine Aufgabe gilt ab dem Ziel als erledigt`() {
        val quest = DailyQuests.pool.first { it.kind == QuestKind.XP }
        assertFalse(DailyQuests.isDone(quest, UserProgress(xpToday = quest.goal - 1)))
        assertTrue(DailyQuests.isDone(quest, UserProgress(xpToday = quest.goal)))
        assertTrue(DailyQuests.isDone(quest, UserProgress(xpToday = quest.goal + 50)))
    }

    @Test
    fun `die Tages-Truhe gibt es erst, wenn alle drei erledigt sind`() {
        val leer = UserProgress()
        assertFalse(DailyQuests.allDone(tag, leer))

        val voll = UserProgress(
            xpToday = 100,
            lessonsToday = 10,
            perfectToday = 5,
            wordsToday = 50,
            correctToday = 50,
            reviewedToday = 50
        )
        assertTrue(DailyQuests.allDone(tag, voll))
    }
}
