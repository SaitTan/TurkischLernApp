package de.turkischlernen.app

import de.turkischlernen.app.data.content.MascotPhrase
import de.turkischlernen.app.data.content.MascotPhrases
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MascotPhrasesTest {

    @Test
    fun `jede Kategorie hat mehrere Rufe`() {
        assertTrue(MascotPhrases.success.size >= 3)
        assertTrue(MascotPhrases.comfort.size >= 3)
        assertTrue(MascotPhrases.cheer.size >= 3)
    }

    @Test
    fun `jeder Ruf hat tuerkischen Text und deutsche Uebersetzung`() {
        val alle = MascotPhrases.success + MascotPhrases.comfort + MascotPhrases.cheer
        alle.forEach { phrase ->
            assertTrue("Türkisch fehlt: $phrase", phrase.tr.isNotBlank())
            assertTrue("Übersetzung fehlt: $phrase", phrase.de.isNotBlank())
        }
    }

    @Test
    fun `derselbe Ruf kommt nie zweimal hintereinander`() {
        val random = Random(4)
        var previous: MascotPhrase? = null
        repeat(200) {
            val phrase = MascotPhrases.pick(MascotPhrases.success, previous, random)
            assertNotEquals(previous, phrase)
            assertTrue(phrase in MascotPhrases.success)
            previous = phrase
        }
    }

    @Test
    fun `aus einer einzigen Moeglichkeit kommt genau diese`() {
        val nur = listOf(MascotPhrase("Harika!", "Toll!"))
        assertEquals(nur.first(), MascotPhrases.pick(nur, null, Random(1)))
    }
}
