package de.turkischlernen.app

import de.turkischlernen.app.data.progress.LevelLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelLogicTest {

    @Test
    fun `hundert XP ergeben ein Level`() {
        assertEquals(1, LevelLogic.levelOf(0))
        assertEquals(1, LevelLogic.levelOf(99))
        assertEquals(2, LevelLogic.levelOf(100))
        assertEquals(3, LevelLogic.levelOf(250))
    }

    @Test
    fun `Rest-XP und fehlende XP passen zusammen`() {
        assertEquals(85, LevelLogic.xpIntoLevel(285))
        assertEquals(15, LevelLogic.xpToNext(285))
        assertEquals(
            LevelLogic.XP_PER_LEVEL,
            LevelLogic.xpIntoLevel(285) + LevelLogic.xpToNext(285)
        )
    }

    @Test
    fun `ein Aufstieg wird erkannt`() {
        assertTrue(LevelLogic.leveledUp(before = 95, after = 110))
        assertFalse(LevelLogic.leveledUp(before = 101, after = 150))
        assertFalse(LevelLogic.leveledUp(before = 300, after = 300))
    }

    @Test
    fun `jede Levelstufe hat eine eigene Truhe`() {
        assertEquals("chest_level_4", LevelLogic.chestId(4))
        assertNotEquals(LevelLogic.chestId(5), LevelLogic.chestId(6))
    }

    @Test
    fun `negative Werte ergeben Level eins`() {
        assertEquals(1, LevelLogic.levelOf(-50))
        assertEquals(0, LevelLogic.xpIntoLevel(-50))
    }
}
