package com.saittan.turkischlernen.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saittan.turkischlernen.R
import com.saittan.turkischlernen.audio.AudioManager
import com.saittan.turkischlernen.data.models.Category
import com.saittan.turkischlernen.data.models.Word
import com.saittan.turkischlernen.ui.components.BackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private enum class AnswerState { Idle, Correct, Wrong }

@Composable
fun WordLearningScreen(
    category: Category,
    audio: AudioManager,
    onBack: () -> Unit,
) {
    val words = category.words
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentWord = words[currentIndex]
    val options = remember(currentIndex) { buildOptions(currentWord, words) }
    var answerState by remember(currentIndex) { mutableStateOf(AnswerState.Idle) }
    var pickedOption by remember(currentIndex) { mutableStateOf<String?>(null) }
    var showWordText by remember(currentIndex) { mutableStateOf(false) }

    val imageScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    // Auto-advance after correct answer
    LaunchedEffect(answerState) {
        if (answerState == AnswerState.Correct) {
            delay(1800)
            advance(words.size, currentIndex)?.let { currentIndex = it }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp),
        ) {
            BackBar(title = category.nameDe, onBack = onBack)

            // Bild + Wort
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(32.dp)),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                            ) {
                                showWordText = true
                                audio.speak(currentWord.turkish, currentWord.audioResId)
                                scope.launch {
                                    imageScale.animateTo(1.15f, tween(150))
                                    imageScale.animateTo(1f, tween(200))
                                }
                            }
                            .scale(imageScale.value),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = currentWord.imageResId),
                            contentDescription = currentWord.german,
                            modifier = Modifier.fillMaxSize(),
                        )
                        if (currentWord.known) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(40.dp),
                            )
                        }
                    }
                    if (showWordText || answerState != AnswerState.Idle) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = currentWord.turkish,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Antwort-Optionen
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(options) { option ->
                    AnswerButton(
                        text = option,
                        state = when {
                            answerState == AnswerState.Idle -> ButtonState.Idle
                            option == currentWord.turkish -> ButtonState.Correct
                            option == pickedOption -> ButtonState.Wrong
                            else -> ButtonState.Dimmed
                        },
                        enabled = answerState == AnswerState.Idle,
                        onClick = {
                            pickedOption = option
                            if (option == currentWord.turkish) {
                                answerState = AnswerState.Correct
                                audio.speak(currentWord.turkish, currentWord.audioResId)
                            } else {
                                answerState = AnswerState.Wrong
                                // Show correct answer audio after a beat
                                scope.launch {
                                    delay(600)
                                    audio.speak(currentWord.turkish, currentWord.audioResId)
                                }
                            }
                        },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (answerState == AnswerState.Wrong) {
                Button(
                    onClick = {
                        advance(words.size, currentIndex)?.let { currentIndex = it }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Text(
                        text = "Nächstes Wort →",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }

        if (answerState == AnswerState.Correct) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map { it.toInt() },
                        emitter = Emitter(duration = 600, TimeUnit.MILLISECONDS).max(120),
                        position = Position.Relative(0.5, 0.3),
                    )
                ),
            )
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Super gemacht! 🌟",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }
        }
    }
}

private enum class ButtonState { Idle, Correct, Wrong, Dimmed }

@Composable
private fun AnswerButton(
    text: String,
    state: ButtonState,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val container = when (state) {
        ButtonState.Idle -> MaterialTheme.colorScheme.primary
        ButtonState.Correct -> Color(0xFF4CAF50)
        ButtonState.Wrong -> MaterialTheme.colorScheme.error
        ButtonState.Dimmed -> MaterialTheme.colorScheme.surfaceVariant
    }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(state) {
        if (state == ButtonState.Wrong) {
            scope.launch {
                repeat(3) {
                    shakeOffset.animateTo(12f, tween(50))
                    shakeOffset.animateTo(-12f, tween(50))
                }
                shakeOffset.animateTo(0f, tween(50))
            }
        }
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Color.White,
            disabledContainerColor = container,
            disabledContentColor = Color.White,
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer { translationX = shakeOffset.value },
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun buildOptions(correct: Word, all: List<Word>): List<String> {
    val distractors = all.filter { it.turkish != correct.turkish }
        .shuffled(Random(correct.turkish.hashCode()))
        .take(3)
        .map { it.turkish }
    return (distractors + correct.turkish).shuffled(Random(correct.turkish.hashCode() + 1))
}

private fun advance(total: Int, current: Int): Int? {
    if (total <= 0) return null
    return (current + 1) % total
}
