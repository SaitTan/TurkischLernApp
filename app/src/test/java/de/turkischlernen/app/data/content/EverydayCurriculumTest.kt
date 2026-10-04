package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.model.LessonKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class EverydayCurriculumTest {

    @Test
    fun contentPackContainsTenCompleteUnits() {
        assertEquals(10, EverydayCurriculum.units.size)
        assertEquals(90, EverydayCurriculum.words.size)
        assertEquals(40, EverydayCurriculum.phrases.size)

        EverydayCurriculum.units.forEach { content ->
            assertEquals(9, content.words.size)
            assertEquals(4, content.phrases.size)

            val unit = requireNotNull(Curriculum.unit(content.id))
            assertEquals(6, unit.lessons.size)
            assertEquals(LessonKind.TEST, unit.lessons.last().kind)
            assertEquals("${content.id}_test", unit.lessons.last().id)
        }
    }

    @Test
    fun newUnitsAreAppendedAfterThePreviousExpansion() {
        val expectedIds = ExpandedCurriculum.units.map { it.id } +
            EverydayCurriculum.units.map { it.id }

        assertEquals(
            expectedIds,
            Curriculum.units.takeLast(expectedIds.size).map { it.id }
        )
    }

    @Test
    fun allItemsAreRegisteredInTheirUnits() {
        EverydayCurriculum.units.forEach { content ->
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
    fun allCurriculumIdsRemainUnique() {
        val itemIds = Curriculum.words.map { it.id } +
            Curriculum.phrases.map { it.id }
        val unitIds = Curriculum.units.map { it.id }
        val lessonIds = Curriculum.lessons.map { it.id }

        assertEquals(itemIds.size, itemIds.toSet().size)
        assertEquals(unitIds.size, unitIds.toSet().size)
        assertEquals(lessonIds.size, lessonIds.toSet().size)
    }

    @Test
    fun lessonsContainTheExpectedVocabularyAndSentences() {
        EverydayCurriculum.units.forEach { content ->
            val unit = requireNotNull(Curriculum.unit(content.id))

            content.words.chunked(3).forEachIndexed { index, chunk ->
                val lesson = unit.lessons[index]
                assertEquals("${content.id}_l${index + 1}", lesson.id)
                assertEquals(chunk.map { it.id }, lesson.wordIds)
                assertTrue(lesson.phraseIds.isEmpty())
            }

            content.phrases.chunked(2).forEachIndexed { index, chunk ->
                val lesson = unit.lessons[index + 3]
                assertEquals("${content.id}_l${index + 4}", lesson.id)
                assertEquals(chunk.map { it.id }, lesson.phraseIds)
                assertTrue(lesson.wordIds.isEmpty())
            }

            val exam = unit.lessons.last()
            assertEquals(content.words.map { it.id }, exam.wordIds)
            assertEquals(content.phrases.map { it.id }, exam.phraseIds)
        }
    }

    @Test
    fun everyNewLessonGeneratesExercises() {
        EverydayCurriculum.units.forEach { content ->
            val unit = requireNotNull(Curriculum.unit(content.id))

            unit.lessons.forEach { lesson ->
                repeat(10) { seed ->
                    assertTrue(
                        "Keine Aufgaben für ${lesson.id}, Seed $seed",
                        ExerciseGenerator.forLesson(
                            lesson,
                            Random(seed)
                        ).isNotEmpty()
                    )
                }
            }
        }
    }
}