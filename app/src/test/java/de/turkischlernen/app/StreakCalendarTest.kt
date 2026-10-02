package de.turkischlernen.app

import de.turkischlernen.app.data.progress.StreakCalendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class StreakCalendarTest {

    private val freitag: LocalDate = LocalDate.of(2026, 10, 2)

    @Test
    fun `die Woche geht von Montag bis Sonntag`() {
        val woche = StreakCalendar.weekOf(freitag)
        assertEquals(7, woche.size)
        assertEquals(DayOfWeek.MONDAY, woche.first().dayOfWeek)
        assertEquals(DayOfWeek.SUNDAY, woche.last().dayOfWeek)
        assertTrue(freitag in woche)
    }

    @Test
    fun `auch am Sonntag gehoert der Tag zur selben Woche`() {
        val sonntag = LocalDate.of(2026, 10, 4)
        val woche = StreakCalendar.weekOf(sonntag)
        assertTrue(sonntag in woche)
        assertEquals(LocalDate.of(2026, 9, 28), woche.first())
    }

    @Test
    fun `aktive Tage werden erkannt`() {
        val tage = setOf("2026-10-01", "2026-10-02")
        assertTrue(StreakCalendar.isActive(freitag, tage))
        assertFalse(StreakCalendar.isActive(freitag.plusDays(1), tage))
    }

    @Test
    fun `die laengste Serie zaehlt zusammenhaengende Tage`() {
        val tage = setOf(
            "2026-09-20", "2026-09-21", "2026-09-22",
            "2026-09-25",
            "2026-10-01", "2026-10-02"
        )
        assertEquals(3, StreakCalendar.longestStreak(tage))
    }

    @Test
    fun `ohne Tage gibt es keine Serie`() {
        assertEquals(0, StreakCalendar.longestStreak(emptySet()))
    }

    @Test
    fun `kaputte Eintraege stoeren nicht`() {
        assertEquals(1, StreakCalendar.longestStreak(setOf("kein-datum", "2026-10-02")))
    }

    @Test
    fun `alte Tage werden abgeschnitten`() {
        val alt = freitag.minusDays(200).toString()
        val neu = freitag.toString()
        val getrimmt = StreakCalendar.trim(setOf(alt, neu), freitag)
        assertTrue(neu in getrimmt)
        assertFalse(alt in getrimmt)
    }
}
