package de.turkischlernen.app.ui.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.model.Exercise
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.components.ThickProgressBar
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.launch

/**
 * Der Lektions-Bildschirm: eine Aufgabe nach der anderen, unten die
 * Rückmeldung, oben Fortschritt und Herzen.
 */
@Composable
fun LessonScreen(
    lessonId: String,
    practiceItemIds: List<String>,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val progress by container.progressRepository.progress.collectAsState(initial = UserProgress())

    val viewModel: LessonViewModel = viewModel(
        factory = LessonViewModel.factory(
            repository = container.progressRepository,
            sounds = container.sounds,
            lessonId = lessonId,
            practiceItemIds = practiceItemIds
        )
    )

    var showExitDialog by remember { mutableStateOf(false) }

    val speak: (String, Boolean) -> Unit = { text, slow -> container.speech.speak(text, slow) }

    if (viewModel.finished) {
        LessonCompleteScreen(
            earnedXp = viewModel.earnedXp,
            mistakes = viewModel.mistakes,
            streakDays = progress.streakDays,
            onContinue = onExit
        )
        return
    }

    val exercise = viewModel.current
    if (exercise == null) {
        // Nichts zu üben (z. B. Wiederholen ohne gelernte Wörter).
        Column(
            modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🙂", fontSize = 60.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Hier gibt es gerade nichts zu wiederholen. Lerne zuerst ein paar neue Wörter!",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(20.dp))
            ChunkyButton("ZURÜCK", Modifier.fillMaxWidth()) { onExit() }
        }
        return
    }

    val interaction = remember(exercise) { ExerciseInteraction() }
    val locked = viewModel.answerState != AnswerState.Waiting

    Column(modifier.fillMaxSize()) {

        // Kopfzeile: Abbrechen, Fortschritt, Herzen
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "✕",
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { showExitDialog = true }
            )
            ThickProgressBar(
                fraction = viewModel.progressFraction,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (progress.unlimitedHearts) "❤️ ∞" else "❤️ ${progress.hearts}",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.Red
            )
        }

        Text(
            text = exercise.prompt,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            when (exercise) {
                is Exercise.PictureChoice ->
                    PictureChoiceView(exercise, interaction, locked, speak)

                is Exercise.TranslateToGerman ->
                    TranslateView(exercise, interaction, locked, speak)

                is Exercise.Listening ->
                    ListeningView(exercise, interaction, locked, speak)

                is Exercise.WordBank ->
                    WordBankView(exercise, interaction, locked, speak)

                is Exercise.MatchPairs ->
                    Column {
                        MatchPairsView(
                            exercise = exercise,
                            interaction = interaction,
                            onSpeak = speak,
                            onMismatch = { viewModel.playWrongSound() }
                        )
                    }
            }
        }

        FeedbackBar(
            answerState = viewModel.answerState,
            checkEnabled = isAnswerReady(exercise, interaction),
            onCheck = {
                val correct = isAnswerCorrect(exercise, interaction)
                viewModel.submitAnswer(
                    correct = correct,
                    correctAnswer = correctAnswerText(exercise),
                    itemIds = exercise.itemIds
                )
                speak(spokenText(exercise), false)
            },
            onContinue = { viewModel.next() },
            onReplay = { speak(spokenText(exercise), true) }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Lektion beenden?") },
            text = { Text("Dein Fortschritt in dieser Lektion geht verloren.") },
            confirmButton = {
                TextButton(onClick = onExit) { Text("Beenden") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Weiterlernen") }
            }
        )
    }

    if (!progress.hasHeartsLeft()) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Keine Herzen mehr ❤️") },
            text = {
                Text(
                    "Herzen wachsen von selbst wieder nach. " +
                        "Ihr könnt sie hier auch sofort auffüllen."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { container.progressRepository.refillHearts() }
                }) { Text("Auffüllen") }
            },
            dismissButton = {
                TextButton(onClick = onExit) { Text("Später weiter") }
            }
        )
    }
}

/** Unterer Balken: "Prüfen" bzw. Rückmeldung und "Weiter". */
@Composable
private fun FeedbackBar(
    answerState: AnswerState,
    checkEnabled: Boolean,
    onCheck: () -> Unit,
    onContinue: () -> Unit,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when (answerState) {
        AnswerState.Waiting -> MaterialTheme.colorScheme.background
        AnswerState.Correct -> AppColors.GreenLight
        is AnswerState.Wrong -> AppColors.RedLight
    }

    Surface(color = background, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            when (answerState) {
                AnswerState.Waiting -> Unit

                AnswerState.Correct -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🎉", fontSize = 30.sp)
                    Text(
                        "Super gemacht!",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.GreenDark
                    )
                }

                is AnswerState.Wrong -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🙂", fontSize = 30.sp)
                    Column {
                        Text(
                            "Fast! Richtig ist:",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.RedDark
                        )
                        Text(
                            answerState.correctAnswer,
                            style = MaterialTheme.typography.titleLarge,
                            color = AppColors.RedDark
                        )
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable { onReplay() }
                            .padding(8.dp)
                    ) {
                        Text("🔊", fontSize = 22.sp)
                    }
                }
            }

            if (answerState != AnswerState.Waiting) Spacer(Modifier.height(12.dp))

            if (answerState == AnswerState.Waiting) {
                ChunkyButton(
                    text = "PRÜFEN",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = checkEnabled,
                    onClick = onCheck
                )
            } else {
                ChunkyButton(
                    text = "WEITER",
                    modifier = Modifier.fillMaxWidth(),
                    color = if (answerState is AnswerState.Wrong) AppColors.Red else AppColors.Green,
                    onClick = onContinue
                )
            }
        }
    }
}
