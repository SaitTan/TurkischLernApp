package de.turkischlernen.app

import de.turkischlernen.app.data.settings.AvatarConfig
import de.turkischlernen.app.data.settings.AvatarOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun `die Standard-Figur hat dunkle Haare und kein Brille`() {
        val avatar = AvatarConfig()
        assertEquals("mittel", avatar.skin.id)
        assertEquals("kurz", avatar.hair.id)
        assertEquals("schwarz", avatar.hairColor.id)
        assertEquals("anthrazit", avatar.shirt.id)
        assertFalse(avatar.glasses)
    }

    @Test
    fun `Fellfarbe und Accessoire werden ueber die Id gefunden`() {
        assertEquals("Grau", AvatarOptions.fur("grau").label)
        assertEquals("Mütze", AvatarOptions.accessory("muetze").label)
    }

    @Test
    fun `Figur-Auswahl wird ueber die Id gefunden`() {
        assertEquals("Dunkel", AvatarOptions.skin("dunkel").label)
        assertEquals("Locken", AvatarOptions.hair("locken").label)
        assertEquals("Blond", AvatarOptions.hairColor("blond").label)
        assertEquals("Grün", AvatarOptions.shirt("gruen").label)
    }

    @Test
    fun `unbekannte Ids fallen auf den Standard zurueck`() {
        assertEquals(AvatarOptions.DEFAULT_FUR, AvatarOptions.fur("gibtsnicht").id)
        assertEquals(AvatarOptions.DEFAULT_ACCESSORY, AvatarOptions.accessory("gibtsnicht").id)
        assertEquals(AvatarOptions.DEFAULT_SKIN, AvatarOptions.skin("gibtsnicht").id)
        assertEquals(AvatarOptions.DEFAULT_HAIR, AvatarOptions.hair("gibtsnicht").id)
        assertEquals(AvatarOptions.DEFAULT_HAIR_COLOR, AvatarOptions.hairColor("gibtsnicht").id)
        assertEquals(AvatarOptions.DEFAULT_SHIRT, AvatarOptions.shirt("gibtsnicht").id)
    }

    @Test
    fun `alle Ids sind eindeutig`() {
        listOf(
            AvatarOptions.furs.map { it.id },
            AvatarOptions.accessories.map { it.id },
            AvatarOptions.skins.map { it.id },
            AvatarOptions.hairStyles.map { it.id },
            AvatarOptions.hairColors.map { it.id },
            AvatarOptions.shirts.map { it.id }
        ).forEach { ids ->
            assertEquals(ids.size, ids.toSet().size)
        }
    }

    @Test
    fun `jede Auswahl hat Eintraege mit Anzeigenamen`() {
        AvatarOptions.skins.forEach { assertTrue(it.label.isNotBlank()) }
        AvatarOptions.hairColors.forEach { assertTrue(it.label.isNotBlank()) }
        AvatarOptions.shirts.forEach { assertTrue(it.label.isNotBlank()) }
        AvatarOptions.hairStyles.forEach { assertTrue(it.emoji.isNotBlank()) }
        assertTrue(AvatarOptions.shirts.size >= 5)
        assertTrue(AvatarOptions.hairStyles.size >= 4)
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
