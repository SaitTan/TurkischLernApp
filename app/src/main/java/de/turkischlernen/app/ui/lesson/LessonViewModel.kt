package de.turkischlernen.app.ui.lesson

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.turkischlernen.app.audio.SoundPlayer
import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.content.ExerciseGenerator
import de.turkischlernen.app.data.model.Exercise
import de.turkischlernen.app.data.progress.ProgressLogic
import de.turkischlernen.app.data.progress.ProgressRepository
import kotlinx.coroutines.launch

/** Rückmeldung nach einer beantworteten Aufgabe. */
sealed interface AnswerState {
    data object Waiting : AnswerState
    data object Correct : AnswerState
    data class Wrong(val correctAnswer: String) : AnswerState
}

/**
 * Steuert den Ablauf einer Lektion: Aufgabenreihenfolge, Herzen, XP und das
 * Wiedervorlegen falsch beantworteter Aufgaben am Ende der Runde.
 */
class LessonViewModel(
    private val repository: ProgressRepository,
    private val sounds: SoundPlayer,
    val lessonId: String,
    practiceItemIds: List<String> = emptyList()
) : ViewModel() {

    private val lesson = Curriculum.lesson(lessonId)

    private val queue = mutableStateListOf<Exercise>()

    var solvedCount by mutableIntStateOf(0)
        private set

    var mistakes by mutableIntStateOf(0)
        private set

    var answerState by mutableStateOf<AnswerState>(AnswerState.Waiting)
        private set

    var finished by mutableStateOf(false)
        private set

    var earnedXp by mutableIntStateOf(0)
        private set

    /** Aufgaben, die wegen eines Fehlers erneut kommen. */
    private val repeatQueue = mutableListOf<Exercise>()

    val isPractice: Boolean = lesson == null

    val title: String = lesson?.title ?: "Wiederholen"

    init {
        val generated = if (lesson != null) {
            ExerciseGenerator.forLesson(lesson)
        } else {
            ExerciseGenerator.forPractice(practiceItemIds)
        }
        queue.addAll(generated)
    }

    val current: Exercise? get() = queue.firstOrNull()

    val totalExercises: Int get() = solvedCount + queue.size + repeatQueue.size

    val progressFraction: Float
        get() = if (totalExercises == 0) 1f else solvedCount.toFloat() / totalExercises

    /** Antwort auswerten. [itemIds] sind die geübten Vokabeln. */
    fun submitAnswer(correct: Boolean, correctAnswer: String, itemIds: List<String>) {
        if (answerState != AnswerState.Waiting) return

        if (correct) {
            sounds.correct()
            answerState = AnswerState.Correct
            viewModelScope.launch {
                itemIds.forEach { repository.clearMistake(it) }
            }
        } else {
            sounds.wrong()
            mistakes++
            answerState = AnswerState.Wrong(correctAnswer)
            current?.let { repeatQueue.add(it) }
            viewModelScope.launch {
                itemIds.forEach { repository.addMistake(it) }
                repository.loseHeart()
            }
        }
    }

    /** Kleines Signal bei einem falschen Paar – ohne Herzverlust. */
    fun playWrongSound() = sounds.wrong()

    /** Weiter zur nächsten Aufgabe. */
    fun next() {
        if (queue.isNotEmpty()) {
            queue.removeAt(0)
            if (answerState is AnswerState.Correct) solvedCount++
        }
        answerState = AnswerState.Waiting

        if (queue.isEmpty()) {
            if (repeatQueue.isNotEmpty()) {
                queue.addAll(repeatQueue)
                repeatQueue.clear()
            } else {
                complete()
            }
        }
    }

    private fun complete() {
        if (finished) return
        val baseXp = lesson?.xpReward ?: PRACTICE_XP
        earnedXp = ProgressLogic.lessonXp(baseXp, mistakes)
        finished = true
        sounds.celebrate()
        viewModelScope.launch {
            repository.completeLesson(
                lessonId = lessonId,
                earnedXp = earnedXp,
                mistakes = mistakes,
                practicedItemIds = lesson?.itemIds ?: emptyList()
            )
        }
    }

    companion object {
        const val PRACTICE_ID = "practice"
        private const val PRACTICE_XP = 10

        fun factory(
            repository: ProgressRepository,
            sounds: SoundPlayer,
            lessonId: String,
            practiceItemIds: List<String>
        ) = viewModelFactory {
            initializer {
                LessonViewModel(repository, sounds, lessonId, practiceItemIds)
            }
        }
    }
}
