package de.turkischlernen.app

import de.turkischlernen.app.data.progress.ItemReview
import de.turkischlernen.app.data.progress.ReviewLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ReviewLogicTest {

    private val heute: LocalDate = LocalDate.of(2026, 10, 4)

    @Test
    fun `richtig steigt eine Stufe, falsch faellt eine zurueck`() {
        assertEquals(1, ReviewLogic.nextLevel(0, correct = true))
        assertEquals(3, ReviewLogic.nextLevel(2, correct = true))
        assertEquals(1, ReviewLogic.nextLevel(2, correct = false))
    }

    @Test
    fun `die Stufen bleiben zwischen null und fuenf`() {
        assertEquals(0, ReviewLogic.nextLevel(0, correct = false))
        assertEquals(ReviewLogic.MAX_LEVEL, ReviewLogic.nextLevel(ReviewLogic.MAX_LEVEL, correct = true))
    }

    @Test
    fun `die Abstaende wachsen mit der Stufe`() {
        assertEquals(heute.plusDays(1), ReviewLogic.dueDate(1, heute))
        assertEquals(heute.plusDays(3), ReviewLogic.dueDate(2, heute))
        assertEquals(heute.plusDays(30), ReviewLogic.dueDate(5, heute))
    }

    @Test
    fun `nach einer falschen Antwort kommt das Wort morgen wieder`() {
        val stand = ReviewLogic.afterAnswer(
            review = ItemReview("elma", level = 1, dueDate = heute.toString()),
            itemId = "elma",
            correct = false,
            today = heute
        )
        assertEquals(0, stand.level)
        assertEquals(heute.plusDays(1).toString(), stand.dueDate)
    }

    @Test
    fun `nach einer richtigen Antwort kommt es spaeter wieder`() {
        val stand = ReviewLogic.afterAnswer(null, "elma", correct = true, today = heute)
        assertEquals(1, stand.level)
        assertEquals(heute.plusDays(1).toString(), stand.dueDate)

        val zweiterStand = ReviewLogic.afterAnswer(stand, "elma", correct = true, today = heute)
        assertEquals(2, zweiterStand.level)
        assertEquals(heute.plusDays(3).toString(), zweiterStand.dueDate)
    }

    @Test
    fun `faellig ist, was heute oder frueher dran war`() {
        assertTrue(ReviewLogic.isDue(ItemReview("a", 1, heute.toString()), heute))
        assertTrue(ReviewLogic.isDue(ItemReview("a", 1, heute.minusDays(5).toString()), heute))
        assertFalse(ReviewLogic.isDue(ItemReview("a", 1, heute.plusDays(1).toString()), heute))
    }

    @Test
    fun `das dringendste Wort steht vorne`() {
        val liste = listOf(
            ItemReview("neu", 2, heute.toString()),
            ItemReview("alt", 1, heute.minusDays(9).toString()),
            ItemReview("mittel", 3, heute.minusDays(2).toString())
        )
        val sortiert = ReviewLogic.dueItems(liste, heute).map { it.itemId }
        assertEquals(listOf("alt", "mittel", "neu"), sortiert)
    }

    @Test
    fun `nicht faellige Woerter kommen nicht vor`() {
        val liste = listOf(
            ItemReview("heute", 1, heute.toString()),
            ItemReview("spaeter", 4, heute.plusDays(7).toString())
        )
        assertEquals(listOf("heute"), ReviewLogic.dueItems(liste, heute).map { it.itemId })
    }

    @Test
    fun `Lernstaende werden gespeichert und wieder gelesen`() {
        val stand = listOf(
            ItemReview("elma", 2, heute.toString()),
            ItemReview("kedi", 5, heute.plusDays(30).toString())
        )
        val gelesen = ReviewLogic.decode(ReviewLogic.encode(stand))
        assertEquals(2, gelesen.size)
        assertEquals(5, gelesen["kedi"]?.level)
    }

    @Test
    fun `kaputte Eintraege werden uebersprungen`() {
        val gelesen = ReviewLogic.decode(setOf("elma:2:2026-10-04", "kaputt", "kedi:x:2026-10-04", ":1:2026-10-04"))
        assertEquals(setOf("elma"), gelesen.keys)
    }
}
