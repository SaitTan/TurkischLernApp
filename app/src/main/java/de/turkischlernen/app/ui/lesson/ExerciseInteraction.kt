package de.turkischlernen.app.ui.lesson

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import de.turkischlernen.app.data.content.SpeechMatch
import de.turkischlernen.app.data.model.Exercise

/** Eingabezustand der aktuell sichtbaren Aufgabe. */
class ExerciseInteraction {
    /** Gewählte Antwort bei Multiple-Choice-Aufgaben. */
    var selectedItemId by mutableStateOf<String?>(null)

    /** Reihenfolge der angetippten Wortkacheln (Indizes in [Exercise.WordBank.tiles]). */
    val chosenTiles = mutableStateListOf<Int>()

    /** Bereits gefundene Paare. */
    var matchedCount by mutableIntStateOf(0)

    /** Falsch zusammengetippte Paare – zählt für die Bewertung der Aufgabe. */
    var pairMistakes by mutableIntStateOf(0)

    /** Getippte Antwort bei Schreib-Aufgaben. */
    var typedText by mutableStateOf("")

    /** Was die Spracherkennung verstanden hat. */
    var heardText by mutableStateOf<String?>(null)

    /** Wie oft schon gesprochen wurde (nach zwei Versuchen geht es weiter). */
    var speakAttempts by mutableIntStateOf(0)

    var speakCorrect by mutableStateOf(false)

    /** true, sobald die Sprech-Aufgabe beendet ist. */
    var speakDone by mutableStateOf(false)
}

/** Prüft, ob überhaupt eine Antwort gegeben wurde (Button "Prüfen" aktiv). */
fun isAnswerReady(exercise: Exercise, interaction: ExerciseInteraction): Boolean =
    when (exercise) {
        is Exercise.PictureChoice,
        is Exercise.TranslateToGerman,
        is Exercise.Listening -> interaction.selectedItemId != null

        is Exercise.WordBank -> interaction.chosenTiles.isNotEmpty()
        is Exercise.MatchPairs -> interaction.matchedCount >= exercise.items.size
        is Exercise.Speak -> interaction.speakDone
        is Exercise.Write -> interaction.typedText.isNotBlank()
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

        // Nur fehlerfrei gefundene Paare gelten als richtig gelöst.
        is Exercise.MatchPairs -> interaction.pairMistakes == 0
        is Exercise.Speak -> interaction.speakCorrect
        // Tippfehler und fehlende türkische Sonderzeichen werden verziehen.
        is Exercise.Write ->
            SpeechMatch.similarity(interaction.typedText, exercise.target.tr) >= WRITE_THRESHOLD
    }

/** Ab dieser Ähnlichkeit gilt Geschriebenes als richtig. */
private const val WRITE_THRESHOLD = 0.85

/** Text der richtigen Lösung – wird bei einem Fehler eingeblendet. */
fun correctAnswerText(exercise: Exercise): String = when (exercise) {
    is Exercise.PictureChoice -> exercise.target.tr
    is Exercise.TranslateToGerman -> exercise.target.de
    is Exercise.Listening -> exercise.target.tr
    is Exercise.WordBank -> exercise.target.tr
    is Exercise.MatchPairs -> ""
    is Exercise.Speak -> exercise.target.tr
    is Exercise.Write -> exercise.target.tr
}

/** Text, der nach der Antwort vorgelesen wird (immer Türkisch). */
fun spokenText(exercise: Exercise): String = when (exercise) {
    is Exercise.PictureChoice -> exercise.target.tr
    is Exercise.TranslateToGerman -> exercise.target.tr
    is Exercise.Listening -> exercise.target.tr
    is Exercise.WordBank -> exercise.target.tr
    is Exercise.MatchPairs -> ""
    is Exercise.Speak -> exercise.target.tr
    is Exercise.Write -> exercise.target.tr
}
