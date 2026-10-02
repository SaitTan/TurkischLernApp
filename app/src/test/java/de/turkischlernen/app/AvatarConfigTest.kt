package de.turkischlernen.app

import de.turkischlernen.app.data.settings.AvatarConfig
import de.turkischlernen.app.data.settings.AvatarOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AvatarConfigTest {

    @Test
    fun `der Standard-Hund ist sandfarben ohne Accessoire`() {
        val avatar = AvatarConfig()
        assertEquals("sand", avatar.fur.id)
        assertEquals("keins", avatar.accessory.id)
        assertEquals("Kangal", avatar.name)
    }

    @Test
    fun `Fellfarbe und Accessoire werden ueber die Id gefunden`() {
        assertEquals("Grau", AvatarOptions.fur("grau").label)
        assertEquals("Mütze", AvatarOptions.accessory("muetze").label)
    }

    @Test
    fun `unbekannte Ids fallen auf den Standard zurueck`() {
        assertEquals(AvatarOptions.DEFAULT_FUR, AvatarOptions.fur("gibtsnicht").id)
        assertEquals(AvatarOptions.DEFAULT_ACCESSORY, AvatarOptions.accessory("gibtsnicht").id)
    }

    @Test
    fun `alle Ids sind eindeutig`() {
        val furIds = AvatarOptions.furs.map { it.id }
        val accessoryIds = AvatarOptions.accessories.map { it.id }
        assertEquals(furIds.size, furIds.toSet().size)
        assertEquals(accessoryIds.size, accessoryIds.toSet().size)
    }

    @Test
    fun `jede Fellfarbe hat drei verschiedene Farbtoene`() {
        AvatarOptions.furs.forEach { fur ->
            val toene = setOf(fur.furHex, fur.furDarkHex, fur.creamHex)
            assertEquals("Farbtöne doppelt bei ${fur.id}", 3, toene.size)
            assertTrue("Anzeigename fehlt bei ${fur.id}", fur.label.isNotBlank())
        }
    }

    @Test
    fun `zu lange Namen werden gekuerzt`() {
        assertEquals(AvatarOptions.MAX_NAME_LENGTH, AvatarOptions.cleanName("Kangalkangalkangal").length)
        assertEquals("Kara", AvatarOptions.cleanName("Kara"))
    }
}
