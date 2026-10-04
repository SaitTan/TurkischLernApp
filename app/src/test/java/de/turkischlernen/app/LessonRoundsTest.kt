package de.turkischlernen.app

import de.turkischlernen.app.data.progress.LessonRounds
import de.turkischlernen.app.data.progress.NodeStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonRoundsTest {

    @Test
    fun `eine Lektion ist nach drei Runden fertig`() {
        assertFalse(LessonRounds.isDone(2))
        assertTrue(LessonRounds.isDone(3))
        assertTrue(LessonRounds.isDone(4))
    }

    @Test
    fun `eine Extra-Runde macht die Lektion golden`() {
        assertFalse(LessonRounds.isGold(3))
        assertTrue(LessonRounds.isGold(4))
    }

    @Test
    fun `der Zustand haengt an Runden und Freischaltung`() {
        assertEquals(NodeStage.LOCKED, LessonRounds.stageOf(rounds = 0, unlocked = false))
        assertEquals(NodeStage.OPEN, LessonRounds.stageOf(rounds = 0, unlocked = true))
        assertEquals(NodeStage.IN_PROGRESS, LessonRounds.stageOf(rounds = 1, unlocked = true))
        assertEquals(NodeStage.DONE, LessonRounds.stageOf(rounds = 3, unlocked = true))
        assertEquals(NodeStage.GOLD, LessonRounds.stageOf(rounds = 5, unlocked = true))
    }

    @Test
    fun `der Ring fuellt sich mit jeder Runde`() {
        assertEquals(0f, LessonRounds.ringFraction(0), 0.001f)
        assertEquals(1f / 3f, LessonRounds.ringFraction(1), 0.001f)
        assertEquals(1f, LessonRounds.ringFraction(3), 0.001f)
        assertEquals(1f, LessonRounds.ringFraction(9), 0.001f)
    }

    @Test
    fun `die Karte zeigt die naechste Runde an`() {
        assertEquals("Runde 1 von 3", LessonRounds.nextRoundLabel(0))
        assertEquals("Runde 3 von 3", LessonRounds.nextRoundLabel(2))
        assertEquals("Alle Runden geschafft", LessonRounds.nextRoundLabel(3))
    }

    @Test
    fun `der Knopf heisst je nach Stand anders`() {
        assertEquals("LOS!", LessonRounds.buttonLabel(0))
        assertEquals("WEITER", LessonRounds.buttonLabel(1))
        assertEquals("WIEDERHOLEN", LessonRounds.buttonLabel(3))
    }

    @Test
    fun `Runden werden gespeichert und wieder gelesen`() {
        val runden = mapOf("u_essen_l1" to 2, "u_tiere_l1" to 3)
        assertEquals(runden, LessonRounds.decode(LessonRounds.encode(runden)))
    }

    @Test
    fun `kaputte Eintraege werden uebersprungen`() {
        val gelesen = LessonRounds.decode(setOf("ohne_zahl", "u_essen_l1:2", ":5", "a:b"))
        assertEquals(mapOf("u_essen_l1" to 2), gelesen)
    }

    @Test
    fun `alte abgeschlossene Lektionen zaehlen als voll gespielt`() {
        val gewandert = LessonRounds.withMigratedLessons(
            rounds = mapOf("u_essen_l1" to 1),
            completedLessons = setOf("u_essen_l1", "u_tiere_l1")
        )
        assertEquals(1, gewandert["u_essen_l1"])
        assertEquals(LessonRounds.ROUNDS_PER_LESSON, gewandert["u_tiere_l1"])
    }
}
