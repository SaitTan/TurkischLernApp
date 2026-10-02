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
import de.turkischlernen.app.audio.Haptics
import de.turkischlernen.app.audio.SoundPlayer
import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.content.ExerciseGenerator
import de.turkischlernen.app.data.content.MascotPhrase
import de.turkischlernen.app.data.content.MascotPhrases
import de.turkischlernen.app.data.model.Exercise
import de.turkischlernen.app.data.progress.Achievement
import de.turkischlernen.app.data.progress.Achievements
import de.turkischlernen.app.data.progress.LessonLogic
import de.turkischlernen.app.data.progress.LevelLogic
import de.turkischlernen.app.data.progress.ProgressLogic
import de.turkischlernen.app.data.progress.ProgressRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Rückmeldung nach einer beantworteten Aufgabe. */
sealed interface AnswerState {
    data object Waiting : AnswerState
    data object Correct : AnswerState
    data class Wrong(val correctAnswer: String) : AnswerState
}

/**
 * Steuert den Ablauf einer Lektion: Aufgabenreihenfolge, Herzen, XP, Combo und das
 * Wiedervorlegen falsch beantworteter Aufgaben am Ende der Runde.
 */
class LessonViewModel(
    private val repository: ProgressRepository,
    private val sounds: SoundPlayer,
    private val haptics: Haptics,
    val lessonId: String,
    practiceItemIds: List<String> = emptyList(),
    private val random: Random = Random.Default
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

    /** Richtige Antworten in Folge. */
    var combo by mutableIntStateOf(0)
        private set

    /** Lobspruch der letzten richtigen Antwort. */
    var praise by mutableStateOf(LessonLogic.PRAISES.first())
        private set

    /** Aktueller Zuruf des Maskottchens – null, bis eine Antwort gegeben wurde. */
    var mascotPhrase by mutableStateOf<MascotPhrase?>(null)
        private set

    private var correctAnswers by mutableIntStateOf(0)
    private var totalAnswers by mutableIntStateOf(0)

    val accuracyPercent: Int get() = LessonLogic.accuracyPercent(correctAnswers, totalAnswers)

    /** Durch diese Lektion neu freigeschaltete Abzeichen (nach dem Speichern gesetzt). */
    var newAchievements by mutableStateOf<List<Achievement>>(emptyList())
        private set

    /** Die Serie ist durch diese Lektion gewachsen. */
    var streakIncreased by mutableStateOf(false)
        private set

    /** Neu erreichtes Level – null, wenn diese Runde keinen Aufstieg brachte. */
    var newLevel by mutableStateOf<Int?>(null)
        private set

    /** Abzeichen und Serie sind berechnet. */
    var resultsReady by mutableStateOf(false)
        private set

    /** Aufgaben, die wegen eines Fehlers erneut kommen. */
    private val repeatQueue = mutableListOf<Exercise>()

    /** Vokabeln dieser Runde – werden am Ende als "gelernt" gespeichert. */
    private val practicedIds: List<String> = practiceItemIds

    private var lastPraise: String? = null

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

    /**
     * Antwort auswerten. [itemIds] sind die geübten Vokabeln.
     * [loseHeart] ist false, wenn ein Fehler kein Herz kosten soll (Paare finden).
     */
    fun submitAnswer(
        correct: Boolean,
        correctAnswer: String,
        itemIds: List<String>,
        loseHeart: Boolean = true
    ) {
        if (answerState != AnswerState.Waiting) return

        totalAnswers++
        combo = LessonLogic.nextCombo(combo, correct)

        if (correct) {
            correctAnswers++
            praise = LessonLogic.pickPraise(lastPraise, random).also { lastPraise = it }
            mascotPhrase = MascotPhrases.pick(MascotPhrases.success, mascotPhrase, random)
            // Ab der Combo-Schwelle ersetzt der steigende Combo-Ton den normalen Ton.
            if (LessonLogic.showsComboBanner(combo)) {
                sounds.combo(LessonLogic.comboLevel(combo))
            } else {
                sounds.correct()
            }
            haptics.success()
            answerState = AnswerState.Correct
            viewModelScope.launch {
                // Erst der Prüfungs-Ton, direkt danach die Stimme des Kangal.
                delay(VOICE_DELAY_MS)
                sounds.kangalCorrect()
            }
            viewModelScope.launch {
                itemIds.forEach { repository.clearMistake(it) }
            }
        } else {
            mascotPhrase = MascotPhrases.pick(MascotPhrases.comfort, mascotPhrase, random)
            sounds.wrong()
            haptics.error()
            viewModelScope.launch {
                delay(VOICE_DELAY_MS)
                sounds.kangalWrong()
            }
            mistakes++
            answerState = AnswerState.Wrong(correctAnswer)
            current?.let { repeatQueue.add(it) }
            viewModelScope.launch {
                itemIds.forEach { repository.addMistake(it) }
                if (loseHeart) repository.loseHeart()
            }
        }
    }

    /** Falsches Paar bei "Paare finden" – Signal ohne Herzverlust. */
    fun onPairMismatch() {
        sounds.wrong()
        haptics.error()
    }

    /** Weiter zur nächsten Aufgabe. */
    fun next() {
        if (queue.isNotEmpty()) {
            queue.removeAt(0)
            if (answerState is AnswerState.Correct) solvedCount++
        }
        answerState = AnswerState.Waiting
        mascotPhrase = null

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
        finished = true
        mascotPhrase = MascotPhrases.pick(MascotPhrases.cheer, mascotPhrase, random)
        viewModelScope.launch {
            val before = repository.progress.first()
            // Freies Wiederholen gibt nur bis zum Tageskontingent XP.
            earnedXp = if (lesson == null) {
                ProgressLogic.practiceXp(PRACTICE_XP, before.practiceXpToday)
            } else {
                ProgressLogic.lessonXp(lesson.xpReward, mistakes)
            }
            repository.completeLesson(
                // Freies Wiederholen zählt nicht als abgeschlossene Lektion.
                lessonId = lesson?.id,
                earnedXp = earnedXp,
                mistakes = mistakes,
                practicedItemIds = lesson?.itemIds ?: practicedIds,
                correctAnswers = correctAnswers
            )
            val after = repository.progress.first()
            newAchievements = LessonLogic.newlyUnlocked(
                Achievements.forProgress(before),
                Achievements.forProgress(after)
            )
            streakIncreased = after.streakDays > before.streakDays

            // Level steigt mit den XP; der Aufstieg bringt zusätzliche Spielzeit.
            if (LevelLogic.leveledUp(before.totalXp, after.totalXp)) {
                newLevel = LevelLogic.levelOf(after.totalXp)
                repository.addPlayMinutes(LevelLogic.LEVEL_UP_PLAY_MINUTES)
            }

            resultsReady = true
        }
    }

    companion object {
        const val PRACTICE_ID = "practice"
        private const val PRACTICE_XP = 10

        /** Abstand zwischen Prüfungs-Ton und Kangal-Stimme. */
        private const val VOICE_DELAY_MS = 420L

        fun factory(
            repository: ProgressRepository,
            sounds: SoundPlayer,
            haptics: Haptics,
            lessonId: String,
            practiceItemIds: List<String>
        ) = viewModelFactory {
            initializer {
                LessonViewModel(repository, sounds, haptics, lessonId, practiceItemIds)
            }
        }
    }
}
