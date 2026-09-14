package de.turkischlernen.app

import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.content.ExerciseGenerator
import de.turkischlernen.app.data.model.Exercise
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ExerciseGeneratorTest {

    @Test
    fun `jede Lektion erzeugt Aufgaben`() {
        Curriculum.lessons.forEach { lesson ->
            val exercises = ExerciseGenerator.forLesson(lesson, Random(1))
            assertTrue("Keine Aufgaben für ${lesson.id}", exercises.isNotEmpty())
        }
    }

    @Test
    fun `die Loesung ist immer unter den Antwortmoeglichkeiten`() {
        Curriculum.lessons.forEach { lesson ->
            ExerciseGenerator.forLesson(lesson, Random(7)).forEach { exercise ->
                when (exercise) {
                    is Exercise.PictureChoice -> {
                        assertEquals(4, exercise.options.size)
                        assertTrue(exercise.options.any { it.id == exercise.target.id })
                        assertEquals(4, exercise.options.map { it.id }.toSet().size)
                    }

                    is Exercise.TranslateToGerman -> {
                        assertTrue(exercise.options.any { it.id == exercise.target.id })
                    }

                    is Exercise.Listening -> {
                        assertTrue(exercise.options.any { it.id == exercise.target.id })
                    }

                    is Exercise.WordBank -> {
                        exercise.solution.forEach { token ->
                            assertTrue("$token fehlt im Wortvorrat", token in exercise.tiles)
                        }
                    }

                    is Exercise.MatchPairs -> assertEquals(4, exercise.items.size)
                }
            }
        }
    }

    @Test
    fun `die erste Aufgabe fuehrt ein Wort mit Bild ein`() {
        val lesson = Curriculum.lessons.first { it.wordIds.isNotEmpty() }
        val first = ExerciseGenerator.forLesson(lesson, Random(3)).first()
        assertTrue(first is Exercise.PictureChoice)
    }

    @Test
    fun `Wiederholen erzeugt Aufgaben zu den uebergebenen Woertern`() {
        val ids = listOf("elma", "kedi", "mavi")
        val exercises = ExerciseGenerator.forPractice(ids, Random(11))
        assertEquals(3, exercises.size)
        assertTrue(exercises.all { it.itemIds.first() in ids })
    }

    @Test
    fun `Wiederholen ohne Woerter liefert nichts`() {
        assertTrue(ExerciseGenerator.forPractice(emptyList(), Random(1)).isEmpty())
    }
}
