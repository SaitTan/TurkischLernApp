package de.turkischlernen.app

import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.content.Situations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CurriculumTest {

    @Test
    fun `alle IDs sind eindeutig`() {
        val ids = Curriculum.words.map { it.id } + Curriculum.phrases.map { it.id }
        assertEquals(ids.size, ids.toSet().size)

        val lessonIds = Curriculum.lessons.map { it.id }
        assertEquals(lessonIds.size, lessonIds.toSet().size)
    }

    @Test
    fun `jede Lektion verweist auf vorhandene Vokabeln`() {
        Curriculum.lessons.forEach { lesson ->
            assertTrue("Lektion ${lesson.id} ist leer", lesson.itemIds.isNotEmpty())
            lesson.itemIds.forEach { id ->
                assertNotNull("Unbekannte Vokabel $id in ${lesson.id}", Curriculum.item(id))
            }
        }
    }

    @Test
    fun `jede Einheit endet mit einer Pruefung`() {
        Curriculum.units.forEach { unit ->
            assertTrue(unit.lessons.isNotEmpty())
            assertTrue(unit.lessons.last().id.endsWith("_test"))
        }
    }

    @Test
    fun `die geforderten Startkategorien sind vollstaendig`() {
        val required = mapOf(
            "u_essen" to listOf("elma", "su", "ekmek", "muz", "sut"),
            "u_tiere" to listOf("kopek", "kedi", "kus", "balik", "at"),
            "u_natur" to listOf("agac", "cicek", "gunes", "bahce", "yagmur"),
            "u_zuhause" to listOf("ev", "kapi", "masa", "sandalye", "yatak"),
            "u_farben" to listOf("kirmizi", "mavi", "sari", "yesil", "beyaz")
        )
        required.forEach { (unitId, wordIds) ->
            val unit = Curriculum.unit(unitId)
            assertNotNull("Einheit $unitId fehlt", unit)
            wordIds.forEach { id ->
                assertTrue("$id fehlt in $unitId", id in unit!!.wordIds)
            }
        }
    }

    @Test
    fun `bereits bekannte Woerter sind markiert`() {
        listOf("su", "bahce", "yemek", "aciktim", "susadim").forEach { id ->
            assertTrue("$id sollte als bekannt markiert sein", Curriculum.word(id)?.known == true)
        }
    }

    @Test
    fun `alle geforderten Situationskarten sind vorhanden`() {
        val expected = listOf(
            "Tuvalete gitmem lazım", "Acıktım", "Susadım", "Su istiyorum", "Yoruldum",
            "Oynamak istiyorum", "Bittim", "Lütfen yardım et", "Anlamıyorum",
            "Teşekkür ederim"
        )
        val actual = Situations.all.map { it.tr }
        expected.forEach { assertTrue("$it fehlt", it in actual) }
    }

    @Test
    fun `jede Vokabel hat Bild und Aussprachehilfe`() {
        Curriculum.words.forEach {
            assertTrue("Emoji fehlt bei ${it.id}", it.emoji.isNotBlank())
            assertTrue("Aussprache fehlt bei ${it.id}", it.hint.isNotBlank())
        }
        Curriculum.phrases.forEach {
            assertTrue("Emoji fehlt bei ${it.id}", it.emoji.isNotBlank())
            assertTrue("Satz ist zu kurz: ${it.id}", it.tokens.size >= 2)
        }
    }
}
