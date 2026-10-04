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

                    is Exercise.Speak ->
                        assertTrue("Sprech-Aufgabe ohne Wort", exercise.target.tr.isNotBlank())

                    is Exercise.Write ->
                        assertTrue("Schreib-Aufgabe ohne Wort", exercise.target.tr.isNotBlank())
                }
            }
        }
    }

    @Test
    fun `jede Runde ist lang genug, aber nicht endlos`() {
        Curriculum.lessons.forEach { lesson ->
            val exercises = ExerciseGenerator.forLesson(lesson, Random(5))
            assertTrue(
                "${lesson.id} hat nur ${exercises.size} Aufgaben",
                exercises.size >= 28
            )
            assertTrue(
                "${lesson.id} hat ${exercises.size} Aufgaben",
                exercises.size <= 34
            )
        }
    }

    @Test
    fun `eine Runde fragt alle Vokabeln der Lektion und ihre Wiederholung ab`() {
        Curriculum.lessons.forEach { lesson ->
            val exercises = ExerciseGenerator.forLesson(lesson, Random(13))
            val vokabeln = exercises.flatMap { it.itemIds }.distinct()
            val erwartet = lesson.itemIds.size + Curriculum.reviewItems(lesson).size
            assertEquals(
                "${lesson.id} fragt ${vokabeln.size} statt $erwartet Vokabeln ab",
                erwartet,
                vokabeln.size
            )
            assertTrue("${lesson.id} fragt zu wenig ab", vokabeln.size >= 5)
        }
    }

    @Test
    fun `eine Lektion fragt nur Vokabeln ihrer eigenen Einheit ab`() {
        Curriculum.lessons.forEach { lesson ->
            ExerciseGenerator.forLesson(lesson, Random(21)).forEach { exercise ->
                exercise.itemIds.forEach { id ->
                    assertEquals(
                        "$id gehört nicht zu ${lesson.unitId} (${lesson.id})",
                        lesson.unitId,
                        Curriculum.unitIdOfItem(id)
                    )
                }
            }
        }
    }

    @Test
    fun `die falschen Antworten kommen aus derselben Einheit`() {
        Curriculum.lessons.forEach { lesson ->
            ExerciseGenerator.forLesson(lesson, Random(33)).forEach { exercise ->
                val optionen = when (exercise) {
                    is Exercise.PictureChoice -> exercise.options
                    is Exercise.Listening -> exercise.options
                    else -> emptyList()
                }
                optionen.forEach { option ->
                    assertEquals(
                        "Antwort ${option.id} passt nicht zu ${lesson.unitId}",
                        lesson.unitId,
                        Curriculum.unitIdOfItem(option.id)
                    )
                }
            }
        }
    }

    @Test
    fun `jede Lektion enthaelt eine Sprech-Aufgabe`() {
        Curriculum.lessons.forEach { lesson ->
            val exercises = ExerciseGenerator.forLesson(lesson, Random(9))
            assertTrue(
                "Keine Sprech-Aufgabe in ${lesson.id}",
                exercises.any { it is Exercise.Speak }
            )
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
