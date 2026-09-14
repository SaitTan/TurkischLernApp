package de.turkischlernen.app

import de.turkischlernen.app.data.progress.ProgressLogic
import de.turkischlernen.app.data.progress.UserProgress
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ProgressLogicTest {

    private val today: LocalDate = LocalDate.of(2026, 3, 15)

    @Test
    fun `Serie waechst nach einem Tag Pause nicht`() {
        assertEquals(1, ProgressLogic.nextStreak("2026-03-10", 4, today))
    }

    @Test
    fun `Serie waechst am Folgetag`() {
        assertEquals(5, ProgressLogic.nextStreak("2026-03-14", 4, today))
    }

    @Test
    fun `Serie bleibt am selben Tag gleich`() {
        assertEquals(4, ProgressLogic.nextStreak("2026-03-15", 4, today))
    }

    @Test
    fun `erste Lektion startet die Serie`() {
        assertEquals(1, ProgressLogic.nextStreak("", 0, today))
    }

    @Test
    fun `Tages-XP werden am selben Tag addiert`() {
        assertEquals(25, ProgressLogic.nextXpToday("2026-03-15", 10, 15, today))
        assertEquals(15, ProgressLogic.nextXpToday("2026-03-14", 10, 15, today))
    }

    @Test
    fun `fehlerfreie Lektion gibt Bonus-XP`() {
        assertEquals(20, ProgressLogic.lessonXp(15, 0))
        assertEquals(15, ProgressLogic.lessonXp(15, 2))
    }

    /** Beispielzeitpunkt (März 2026) – wie ein echter Zeitstempel. */
    private val now = 1_773_000_000_000L

    @Test
    fun `Herzen wachsen mit der Zeit nach`() {
        val progress = UserProgress(
            hearts = 2,
            heartsUpdatedAt = now - 2 * UserProgress.HEART_REFILL_MILLIS
        )
        assertEquals(4, ProgressLogic.regeneratedHearts(progress, now))
    }

    @Test
    fun `Herzen ueberschreiten nie das Maximum`() {
        val progress = UserProgress(
            hearts = 4,
            heartsUpdatedAt = now - 50 * UserProgress.HEART_REFILL_MILLIS
        )
        assertEquals(UserProgress.MAX_HEARTS, ProgressLogic.regeneratedHearts(progress, now))
    }

    @Test
    fun `ohne gespeicherten Zeitstempel bleibt der Herzstand gleich`() {
        val progress = UserProgress(hearts = 3, heartsUpdatedAt = 0L)
        assertEquals(3, ProgressLogic.regeneratedHearts(progress, now))
    }
}
