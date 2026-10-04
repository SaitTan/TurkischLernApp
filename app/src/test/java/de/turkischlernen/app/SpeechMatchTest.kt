package de.turkischlernen.app

import de.turkischlernen.app.data.content.SpeechMatch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpeechMatchTest {

    @Test
    fun `Gross-Kleinschreibung und Satzzeichen stoeren nicht`() {
        assertEquals("cay", SpeechMatch.normalize("Çay!"))
        assertEquals("su istiyorum", SpeechMatch.normalize("  Su, istiyorum?  "))
    }

    @Test
    fun `tuerkische Sonderzeichen werden vereinfacht`() {
        assertEquals("kopek", SpeechMatch.normalize("köpek"))
        assertEquals("yagmur", SpeechMatch.normalize("yağmur"))
        assertEquals("balik", SpeechMatch.normalize("balık"))
    }

    @Test
    fun `dasselbe Wort ergibt volle Aehnlichkeit`() {
        assertEquals(1.0, SpeechMatch.similarity("elma", "Elma."), 0.001)
    }

    @Test
    fun `ein Buchstabe daneben gilt noch als richtig`() {
        assertTrue(SpeechMatch.matches("elman", "elma"))
        assertTrue(SpeechMatch.matches("kopek", "köpek"))
    }

    @Test
    fun `ein anderes Wort faellt durch`() {
        assertFalse(SpeechMatch.matches("köpek", "elma"))
        assertFalse(SpeechMatch.matches("merhaba", "teşekkür ederim"))
    }

    @Test
    fun `ohne Eingabe ist nichts richtig`() {
        assertFalse(SpeechMatch.matches("", "elma"))
        assertFalse(SpeechMatch.matches("   ", "elma"))
        assertEquals(0.0, SpeechMatch.similarity("", "elma"), 0.001)
    }

    @Test
    fun `ganze Saetze werden ebenfalls verglichen`() {
        assertTrue(SpeechMatch.matches("su istiyorum", "Su istiyorum"))
        assertTrue(SpeechMatch.matches("Su istiyorum.", "su istiyorum"))
        assertFalse(SpeechMatch.matches("ekmek istiyorum", "lütfen yardım et"))
    }
}
