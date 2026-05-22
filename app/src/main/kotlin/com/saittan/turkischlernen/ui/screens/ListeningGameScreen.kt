package com.saittan.turkischlernen.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saittan.turkischlernen.audio.AudioManager
import com.saittan.turkischlernen.data.models.Word
import com.saittan.turkischlernen.data.repository.WordRepository
import com.saittan.turkischlernen.ui.components.BackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private const val ROUNDS_PER_GAME = 10

private data class Round(val correct: Word, val options: List<Word>)

private enum class RoundState { Waiting, Correct, Wrong }

@Composable
fun ListeningGameScreen(
    audio: AudioManager,
    onBack: () -> Unit,
) {
    val allWords = remember { WordRepository.categories.flatMap { it.words } }
    var rounds by remember { mutableStateOf(buildRounds(allWords)) }
    var roundIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf(RoundState.Waiting) }
    var pickedWord by remember { mutableStateOf<Word?>(null) }
    val scope = rememberCoroutineScope()

    val gameDone = roundIndex >= rounds.size
    val currentRound = rounds.getOrNull(roundIndex)

    // Auto-play audio at start of each round
    LaunchedEffect(roundIndex) {
        if (!gameDone) {
            delay(300)
            currentRound?.correct?.let { audio.speak(it.turkish, it.audioResId) }
        }
    }

    // Advance after answer
    LaunchedEffect(state) {
        if (state == RoundState.Correct || state == RoundState.Wrong) {
            delay(1500)
            pickedWord = null
            state = RoundState.Waiting
            roundIndex++
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 16.dp),
        ) {
            BackBar(title = "Hörspiel", onBack = onBack)
            ScoreRow(round = roundIndex.coerceAtMost(rounds.size), total = rounds.size, score = score)

            if (gameDone) {
                GameDoneView(
                    score = score,
                    total = rounds.size,
                    onReplay = {
                        rounds = buildRounds(allWords)
                        roundIndex = 0
                        score = 0
                        state = RoundState.Waiting
                        pickedWord = null
                    },
                    onBack = onBack,
                )
            } else if (currentRound != null) {
                Spacer(Modifier.height(8.dp))
                PlayAudioButton(
                    enabled = state == RoundState.Waiting,
                    onClick = {
                        audio.speak(currentRound.correct.turkish, currentRound.correct.audioResId)
                    },
                )
                Spacer(Modifier.height(20.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(currentRound.options) { option ->
                        val tileState = when {
                            state == RoundState.Waiting -> TileState.Idle
                            option.turkish == currentRound.correct.turkish -> TileState.Correct
                            option.turkish == pickedWord?.turkish -> TileState.Wrong
                            else -> TileState.Dimmed
                        }
                        ImageOption(
                            word = option,
                            state = tileState,
                            enabled = state == RoundState.Waiting,
                            onClick = {
                                pickedWord = option
                                if (option.turkish == currentRound.correct.turkish) {
                                    score++
                                    state = RoundState.Correct
                                    audio.speak(currentRound.correct.turkish, currentRound.correct.audioResId)
                                } else {
                                    state = RoundState.Wrong
                                    scope.launch {
                                        delay(400)
                                        audio.speak(currentRound.correct.turkish, currentRound.correct.audioResId)
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }

        if (state == RoundState.Correct) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 25f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xFFFCE18A.toInt(), 0xFFFF726D.toInt(), 0xFFF4306D.toInt(), 0xFFB48DEF.toInt()),
                        emitter = Emitter(duration = 400, TimeUnit.MILLISECONDS).max(80),
                        position = Position.Relative(0.5, 0.3),
                    )
                ),
            )
        }
    }
}

@Composable
private fun ScoreRow(round: Int, total: Int, score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Runde $round / $total",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Surface(
            color = MaterialTheme.colorScheme.secondary,
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(
                text = "⭐ $score",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun PlayAudioButton(enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilledIconButton(
            onClick = onClick,
            enabled = enabled,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
            ),
            modifier = Modifier.size(96.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.VolumeUp,
                contentDescription = "Wieder anhören",
                modifier = Modifier.size(48.dp),
            )
        }
        Spacer(Modifier.size(12.dp))
        Text(
            text = "Was hörst\ndu?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )
    }
}

private enum class TileState { Idle, Correct, Wrong, Dimmed }

@Composable
private fun ImageOption(
    word: Word,
    state: TileState,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = when (state) {
        TileState.Correct -> Color(0xFF4CAF50)
        TileState.Wrong -> MaterialTheme.colorScheme.error
        TileState.Dimmed -> Color.Transparent
        TileState.Idle -> Color.Transparent
    }
    val alpha = if (state == TileState.Dimmed) 0.4f else 1f
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = if (state == TileState.Correct) Color(0xFFE8F5E9)
            else if (state == TileState.Wrong) Color(0xFFFFEBEE)
            else MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        border = if (borderColor != Color.Transparent)
            androidx.compose.foundation.BorderStroke(4.dp, borderColor) else null,
    ) {
        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = word.imageResId),
                contentDescription = word.german,
                modifier = Modifier.fillMaxSize().alpha(alpha),
            )
            if (state == TileState.Correct || state == TileState.Wrong) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Text(
                        text = word.turkish,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (state == TileState.Correct) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun GameDoneView(
    score: Int,
    total: Int,
    onReplay: () -> Unit,
    onBack: () -> Unit,
) {
    val message = when {
        score == total -> "Perfekt! 🌟🌟🌟"
        score >= total * 0.7 -> "Klasse! 🌟🌟"
        score >= total * 0.4 -> "Gut gemacht! 🌟"
        else -> "Weiter üben!"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Du hast $score von $total richtig.",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onReplay,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(24.dp),
        ) {
            Text(text = "Nochmal spielen", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(24.dp),
        ) {
            Text(text = "Zum Hauptmenü", style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun buildRounds(allWords: List<Word>): List<Round> {
    val seed = System.nanoTime()
    val rng = Random(seed)
    val correctWords = allWords.shuffled(rng).take(ROUNDS_PER_GAME)
    return correctWords.map { correct ->
        val distractors = allWords.filter { it.turkish != correct.turkish }
            .shuffled(rng)
            .take(3)
        val options = (distractors + correct).shuffled(rng)
        Round(correct = correct, options = options)
    }
}
