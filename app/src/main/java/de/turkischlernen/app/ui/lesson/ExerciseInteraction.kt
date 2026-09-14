package de.turkischlernen.app.ui.lesson

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import de.turkischlernen.app.data.model.Exercise

/** Eingabezustand der aktuell sichtbaren Aufgabe. */
class ExerciseInteraction {
    /** Gewählte Antwort bei Multiple-Choice-Aufgaben. */
    var selectedItemId by mutableStateOf<String?>(null)

    /** Reihenfolge der angetippten Wortkacheln (Indizes in [Exercise.WordBank.tiles]). */
    val chosenTiles = mutableStateListOf<Int>()

    /** Bereits gefundene Paare. */
    var matchedCount by mutableIntStateOf(0)
}

/** Prüft, ob überhaupt eine Antwort gegeben wurde (Button "Prüfen" aktiv). */
fun isAnswerReady(exercise: Exercise, interaction: ExerciseInteraction): Boolean =
    when (exercise) {
        is Exercise.PictureChoice,
        is Exercise.TranslateToGerman,
        is Exercise.Listening -> interaction.selectedItemId != null

        is Exercise.WordBank -> interaction.chosenTiles.isNotEmpty()
        is Exercise.MatchPairs -> interaction.matchedCount >= exercise.items.size
    }

/** Wertet die Antwort aus. */
fun isAnswerCorrect(exercise: Exercise, interaction: ExerciseInteraction): Boolean =
    when (exercise) {
        is Exercise.PictureChoice -> interaction.selectedItemId == exercise.target.id
        is Exercise.TranslateToGerman -> interaction.selectedItemId == exercise.target.id
        is Exercise.Listening -> interaction.selectedItemId == exercise.target.id
        is Exercise.WordBank -> {
            val built = interaction.chosenTiles.map { exercise.tiles[it] }
            built.size == exercise.solution.size &&
                built.zip(exercise.solution).all { (a, b) -> a.equals(b, ignoreCase = true) }
        }

        is Exercise.MatchPairs -> true
    }

/** Text der richtigen Lösung – wird bei einem Fehler eingeblendet. */
fun correctAnswerText(exercise: Exercise): String = when (exercise) {
    is Exercise.PictureChoice -> exercise.target.tr
    is Exercise.TranslateToGerman -> exercise.target.de
    is Exercise.Listening -> exercise.target.tr
    is Exercise.WordBank -> exercise.target.tr
    is Exercise.MatchPairs -> ""
}

/** Text, der nach der Antwort vorgelesen wird (immer Türkisch). */
fun spokenText(exercise: Exercise): String = when (exercise) {
    is Exercise.PictureChoice -> exercise.target.tr
    is Exercise.TranslateToGerman -> exercise.target.tr
    is Exercise.Listening -> exercise.target.tr
    is Exercise.WordBank -> exercise.target.tr
    is Exercise.MatchPairs -> ""
}
