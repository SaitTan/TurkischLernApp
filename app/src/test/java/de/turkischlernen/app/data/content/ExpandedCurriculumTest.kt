package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.model.LessonKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ExpandedCurriculumTest {

    @Test
    fun expansionContainsFifteenCompleteUnits() {
        assertEquals(15, ExpandedCurriculum.units.size)
        assertEquals(135, ExpandedCurriculum.words.size)
        assertEquals(30, ExpandedCurriculum.phrases.size)

        ExpandedCurriculum.units.forEach { content ->
            assertEquals(9, content.words.size)
            assertEquals(2, content.phrases.size)

            val unit = requireNotNull(Curriculum.unit(content.id))
            assertEquals(5, unit.lessons.size)
            assertEquals(LessonKind.TEST, unit.lessons.last().kind)
            assertEquals("${content.id}_test", unit.lessons.last().id)
        }
    }

    @Test
    fun allCurriculumIdsAreUnique() {
        val itemIds = Curriculum.words.map { it.id } +
            Curriculum.phrases.map { it.id }
        val unitIds = Curriculum.units.map { it.id }
        val lessonIds = Curriculum.lessons.map { it.id }

        assertEquals(itemIds.size, itemIds.toSet().size)
        assertEquals(unitIds.size, unitIds.toSet().size)
        assertEquals(lessonIds.size, lessonIds.toSet().size)
    }

    @Test
    fun everyExpandedItemIsRegisteredInItsUnit() {
        ExpandedCurriculum.units.forEach { content ->
            content.words.forEach { word ->
                assertEquals(word, Curriculum.word(word.id))
                assertEquals(content.id, Curriculum.unitIdOfItem(word.id))
            }
            content.phrases.forEach { phrase ->
                assertEquals(phrase, Curriculum.phrase(phrase.id))
                assertEquals(content.id, Curriculum.unitIdOfItem(phrase.id))
            }
        }
    }

    @Test
    fun expandedLessonsGenerateExercises() {
        ExpandedCurriculum.units.forEach { content ->
            val unit = requireNotNull(Curriculum.unit(content.id))

            unit.lessons.forEach { lesson ->
                assertTrue(
                    "Keine Aufgaben für ${lesson.id}",
                    ExerciseGenerator.forLesson(lesson, Random(42)).isNotEmpty()
                )

                lesson.wordIds.forEach { id ->
                    assertNotNull(Curriculum.word(id))
                }
                lesson.phraseIds.forEach { id ->
                    assertNotNull(Curriculum.phrase(id))
                }
            }

            val exam = unit.lessons.last()
            assertEquals(content.words.map { it.id }, exam.wordIds)
            assertEquals(content.phrases.map { it.id }, exam.phraseIds)
        }
    }

    @Test
    fun previouslyProposedVocabularyLessonIdsRemainStable() {
        val expectedLessons = mapOf(
            "u_schule_l1" to listOf("okul", "ogretmen", "ogrenci"),
            "u_schule_l2" to listOf("kalem", "defter", "silgi"),
            "u_schule_l3" to listOf("canta", "cetvel", "makas"),
            "u_koerper_l1" to listOf("bas", "goz", "kulak"),
            "u_koerper_l2" to listOf("burun", "agiz", "dis"),
            "u_koerper_l3" to listOf("el", "ayak", "bacak"),
            "u_kleidung_l1" to listOf("tisort", "pantolon", "elbise"),
            "u_kleidung_l2" to listOf("ayakkabi", "corap", "sapka"),
            "u_kleidung_l3" to listOf("mont", "eldiven", "atki")
        )

        expectedLessons.forEach { (lessonId, wordIds) ->
            assertEquals(wordIds, Curriculum.lesson(lessonId)?.wordIds)
        }
    }
}