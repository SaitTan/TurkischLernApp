package de.turkischlernen.app

import de.turkischlernen.app.data.progress.ChestLogic
import de.turkischlernen.app.data.progress.ChestReward
import de.turkischlernen.app.data.settings.AvatarOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class ChestLogicTest {

    @Test
    fun `nach jeder dritten Lektion steht eine Truhe`() {
        assertFalse(ChestLogic.chestAfter(0))
        assertFalse(ChestLogic.chestAfter(1))
        assertFalse(ChestLogic.chestAfter(2))
        assertTrue(ChestLogic.chestAfter(3))
        assertTrue(ChestLogic.chestAfter(6))
        assertFalse(ChestLogic.chestAfter(7))
    }

    @Test
    fun `Truhen-Ids sind stabil und unterscheidbar`() {
        assertEquals("chest_u_essen_l3", ChestLogic.chestId("u_essen_l3"))
        assertEquals("chest_tag_2026-10-02", ChestLogic.dailyChestId(LocalDate.of(2026, 10, 2)))
    }

    @Test
    fun `XP-Gewinne liegen im erlaubten Bereich`() {
        val alleTeileFrei = AvatarOptions.lockedItemIds.toSet()
        repeat(200) { durchlauf ->
            val reward = ChestLogic.roll(Random(durchlauf), alleTeileFrei)
            assertTrue("Erwartet wurden XP, kam: $reward", reward is ChestReward.Xp)
            val xp = (reward as ChestReward.Xp).amount
            assertTrue("XP zu klein: $xp", xp >= ChestLogic.MIN_XP)
            assertTrue("XP zu gross: $xp", xp <= ChestLogic.MAX_XP)
        }
    }

    @Test
    fun `ein bereits freigeschaltetes Teil kommt nie noch einmal`() {
        val schonFrei = AvatarOptions.lockedItemIds.drop(1).toSet()
        repeat(200) { durchlauf ->
            val reward = ChestLogic.roll(Random(durchlauf), schonFrei)
            if (reward is ChestReward.Item) {
                assertEquals(AvatarOptions.lockedItemIds.first(), reward.id)
            }
        }
    }

    @Test
    fun `ohne freigeschaltete Teile kommen Teile und XP vor`() {
        val gefunden = (1..200).map { ChestLogic.roll(Random(it), emptySet()) }
        assertTrue("Kein Teil dabei", gefunden.any { it is ChestReward.Item })
        assertTrue("Keine XP dabei", gefunden.any { it is ChestReward.Xp })
    }

    @Test
    fun `jedes gefundene Teil hat einen Anzeigenamen`() {
        AvatarOptions.lockedItemIds.forEach { id ->
            assertTrue("Name fehlt für $id", AvatarOptions.itemLabel(id).isNotBlank())
            assertFalse("Name ist nur die Id: $id", AvatarOptions.itemLabel(id) == id)
        }
    }
}
