package de.turkischlernen.app

import de.turkischlernen.app.data.progress.Achievement
import de.turkischlernen.app.data.progress.LessonLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class LessonLogicTest {

    @Test
    fun `richtige Antwort erhoeht die Combo`() {
        assertEquals(3, LessonLogic.nextCombo(2, correct = true))
    }

    @Test
    fun `falsche Antwort setzt die Combo zurueck`() {
        assertEquals(0, LessonLogic.nextCombo(7, correct = false))
    }

    @Test
    fun `Combo-Banner erscheint erst ab drei in Folge`() {
        assertFalse(LessonLogic.showsComboBanner(2))
        assertTrue(LessonLogic.showsComboBanner(3))
    }

    @Test
    fun `Combo-Stufe steigt bis maximal fuenf`() {
        assertEquals(0, LessonLogic.comboLevel(2))
        assertEquals(1, LessonLogic.comboLevel(3))
        assertEquals(2, LessonLogic.comboLevel(4))
        assertEquals(5, LessonLogic.comboLevel(7))
        assertEquals(5, LessonLogic.comboLevel(20))
    }

    @Test
    fun `Genauigkeit wird gerundet`() {
        assertEquals(67, LessonLogic.accuracyPercent(correct = 2, total = 3))
        assertEquals(100, LessonLogic.accuracyPercent(correct = 5, total = 5))
        assertEquals(0, LessonLogic.accuracyPercent(correct = 0, total = 4))
    }

    @Test
    fun `ohne Antworten ist die Genauigkeit 100 Prozent`() {
        assertEquals(100, LessonLogic.accuracyPercent(correct = 0, total = 0))
    }

    @Test
    fun `nur neu freigeschaltete Abzeichen werden gemeldet`() {
        val before = listOf(badge("x", true), badge("y", false), badge("z", false))
        val after = listOf(badge("x", true), badge("y", true), badge("z", false))
        assertEquals(listOf("y"), LessonLogic.newlyUnlocked(before, after).map { it.id })
    }

    @Test
    fun `Lobspruch wiederholt sich nie direkt`() {
        val random = Random(42)
        var previous: String? = null
        repeat(200) {
            val praise = LessonLogic.pickPraise(previous, random)
            assertNotEquals(previous, praise)
            assertTrue(praise in LessonLogic.PRAISES)
            previous = praise
        }
    }

    @Test
    fun `XP-Ticks sind auf zehn begrenzt`() {
        assertEquals(0, LessonLogic.xpTickCount(0))
        assertEquals(5, LessonLogic.xpTickCount(5))
        assertEquals(10, LessonLogic.xpTickCount(25))
    }

    private fun badge(id: String, unlocked: Boolean) =
        Achievement(id, "⭐", id, id, unlocked)
}
