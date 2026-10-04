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

    /** So lang ist eine Runde: genug zum Üben, ohne endlos zu werden. */
    private const val TARGET_EXERCISES_PER_LESSON = 28

    /** Sicherheitsnetz gegen Endlosschleifen beim Auffüllen. */
    private const val MAX_FILL_STEPS = 400

    fun forLesson(lesson: Lesson, random: Random = Random.Default): List<Exercise> {
        val words = lesson.wordIds.mapNotNull { Curriculum.word(it) }
        val phrases = lesson.phraseIds.mapNotNull { Curriculum.phrase(it) }
        val items: List<LearnItem> = words + phrases
        if (items.isEmpty()) return emptyList()

        val exercises = mutableListOf<Exercise>()

        // 1. Einführung: jedes neue Wort zuerst mit Bild, jeder Satz mit Bedeutung.
        words.forEach { word ->
            exercises += Exercise.PictureChoice(word, options(word, random))
        }
        phrases.forEach { phrase ->
            exercises += Exercise.TranslateToGerman(phrase, options(phrase, random))
        }

        // 2. Hauptteil: die neuen Wörter und Vokabeln aus früheren Lektionen kommen
        //    reihum in wechselnden Aufgabentypen, bis die Runde lang genug ist.
        val wiederholung = Curriculum.reviewItems(lesson)
        val pool = (items + wiederholung).distinctBy { it.id }

        var schritt = 0
        while (exercises.size < TARGET_EXERCISES_PER_LESSON && schritt < MAX_FILL_STEPS) {
            val item = pool[schritt % pool.size]
            val runde = schritt / pool.size

            exercises += when (runde % 4) {
                0 -> Exercise.TranslateToGerman(item, options(item, random))
                1 -> Exercise.Listening(item, options(item, random))
                2 -> if (item is Phrase) wordBank(item, random) else Exercise.Speak(item)
                else -> if (item is Phrase) {
                    Exercise.TranslateToGerman(item, options(item, random))
                } else {
                    Exercise.PictureChoice(item, options(item, random))
                }
            }
            schritt++
        }

        // 3. Sprechen gehört in jede Runde.
        if (exercises.none { it is Exercise.Speak }) {
            exercises += Exercise.Speak(items.first())
        }

        // 4. In der Prüfung zusätzlich Paare finden.
        val pairPool = items.shuffled(random)
        if (lesson.kind == LessonKind.TEST && pairPool.size >= 4) {
            exercises += Exercise.MatchPairs(pairPool.take(4))
        }

        return exercises
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
