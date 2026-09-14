package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.model.Exercise
import de.turkischlernen.app.data.model.LearnItem
import de.turkischlernen.app.data.model.Lesson
import de.turkischlernen.app.data.model.LessonKind
import de.turkischlernen.app.data.model.Phrase
import kotlin.random.Random

/**
 * Baut aus den Vokabeln einer Lektion eine abwechslungsreiche Aufgabenfolge –
 * so wie in den bekannten Sprachlern-Apps: Bild → Wort, Wort → Bedeutung,
 * Hörverstehen, Sätze bauen und Paare finden.
 */
object ExerciseGenerator {

    private const val OPTION_COUNT = 4

    fun forLesson(lesson: Lesson, random: Random = Random.Default): List<Exercise> {
        val words = lesson.wordIds.mapNotNull { Curriculum.word(it) }
        val phrases = lesson.phraseIds.mapNotNull { Curriculum.phrase(it) }
        val exercises = mutableListOf<Exercise>()

        // 1. Neue Wörter immer zuerst mit Bild einführen.
        words.forEach { word ->
            exercises += Exercise.PictureChoice(word, options(word, random))
        }

        // 2. Danach abwechselnd Bedeutung und Hörverstehen.
        words.forEachIndexed { index, word ->
            exercises += if (index % 2 == 0) {
                Exercise.TranslateToGerman(word, options(word, random))
            } else {
                Exercise.Listening(word, options(word, random))
            }
        }

        // 3. Sätze: erst hören/verstehen, dann selbst zusammenbauen.
        phrases.forEach { phrase ->
            exercises += Exercise.TranslateToGerman(phrase, options(phrase, random))
            exercises += wordBank(phrase, random)
        }

        // 4. In der Prüfung zusätzlich Paare finden.
        val pairPool = (words + phrases).shuffled(random)
        if (lesson.kind == LessonKind.TEST && pairPool.size >= 4) {
            exercises += Exercise.MatchPairs(pairPool.take(4))
        }

        // Die erste Aufgabe bleibt eine Bildaufgabe, der Rest wird gemischt.
        return if (exercises.size > 1) {
            listOf(exercises.first()) + exercises.drop(1).shuffled(random)
        } else {
            exercises
        }
    }

    /**
     * Wiederholungs-Session: gemischte Aufgaben zu bereits gelernten Wörtern,
     * Fehler-Wörter kommen zuerst.
     */
    fun forPractice(
        itemIds: List<String>,
        random: Random = Random.Default,
        maxExercises: Int = 10
    ): List<Exercise> {
        val items = itemIds.mapNotNull { Curriculum.item(it) }
        if (items.isEmpty()) return emptyList()

        return items.take(maxExercises).map { item ->
            when (random.nextInt(if (item is Phrase) 4 else 3)) {
                0 -> Exercise.PictureChoice(item, options(item, random))
                1 -> Exercise.TranslateToGerman(item, options(item, random))
                2 -> Exercise.Listening(item, options(item, random))
                else -> wordBank(item as Phrase, random)
            }
        }
    }

    private fun wordBank(phrase: Phrase, random: Random): Exercise.WordBank {
        val distractors = Curriculum.words
            .filter { it.tr !in phrase.tokens }
            .shuffled(random)
            .take(if (phrase.tokens.size >= 4) 1 else 2)
            .map { it.tr }
        return Exercise.WordBank(phrase, (phrase.tokens + distractors).shuffled(random))
    }

    /**
     * Eine richtige und drei falsche Antwortmöglichkeiten, zufällig gemischt.
     * Die falschen Antworten stammen bevorzugt aus derselben Einheit – so ist
     * die Aufgabe wirklich eine Aufgabe (vier Tiere statt Tier/Farbe/Zahl).
     */
    private fun options(target: LearnItem, random: Random): List<LearnItem> {
        val sameKind: List<LearnItem> =
            if (target is Phrase) Curriculum.phrases else Curriculum.words
        val pool = sameKind.filter { it.id != target.id }
        val unitId = Curriculum.unitIdOfItem(target.id)
        val sameUnit = pool.filter { Curriculum.unitIdOfItem(it.id) == unitId }

        val distractors = (sameUnit.shuffled(random) + pool.shuffled(random))
            .distinctBy { it.id }
            .take(OPTION_COUNT - 1)

        return (distractors + target).shuffled(random)
    }
}
