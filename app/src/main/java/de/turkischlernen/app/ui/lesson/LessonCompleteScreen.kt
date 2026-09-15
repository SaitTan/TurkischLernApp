package de.turkischlernen.app.ui.lesson

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.progress.Achievement
import de.turkischlernen.app.data.progress.LessonLogic
import de.turkischlernen.app.ui.components.AnimatedCounter
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.components.ConfettiOverlay
import de.turkischlernen.app.ui.components.popIn
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.delay

private const val STAGE_TROPHY = 1
private const val STAGE_TITLE = 2
private const val STAGE_XP = 3
private const val STAGE_ACCURACY = 4
private const val STAGE_STREAK = 5
private const val STAGE_DONE = 6

private const val COUNTER_MILLIS = 900

/** Einblendungen nach dem Abschluss, nacheinander per Tippen. */
private sealed interface CelebrationOverlay {
    data class Badge(val achievement: Achievement) : CelebrationOverlay
    data class Streak(val days: Int) : CelebrationOverlay
}

/**
 * Abschlussbildschirm als kleiner Ablauf: Pokal, Titel, Karten nacheinander
 * (XP zählen hoch), danach neue Abzeichen und "Serie verlängert".
 */
@Composable
fun LessonCompleteScreen(
    earnedXp: Int,
    accuracyPercent: Int,
    perfect: Boolean,
    streakDays: Int,
    newAchievements: List<Achievement>,
    streakIncreased: Boolean,
    resultsReady: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sounds = LocalAppContainer.current.sounds

    var stage by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        sounds.celebrate()
        stage = STAGE_TROPHY
        delay(350)
        stage = STAGE_TITLE
        delay(300)
        stage = STAGE_XP
        delay(150)
        stage = STAGE_ACCURACY
        delay(150)
        stage = STAGE_STREAK
        delay(COUNTER_MILLIS.toLong())
        stage = STAGE_DONE
    }

    val overlays = remember(resultsReady, newAchievements, streakIncreased, streakDays) {
        if (!resultsReady) {
            emptyList()
        } else {
            buildList {
                newAchievements.forEach { add(CelebrationOverlay.Badge(it)) }
                if (streakIncreased) add(CelebrationOverlay.Streak(streakDays))
            }
        }
    }
    var overlayIndex by remember { mutableIntStateOf(0) }
    val activeOverlay = if (stage >= STAGE_DONE) overlays.getOrNull(overlayIndex) else null

    LaunchedEffect(activeOverlay) {
        when (activeOverlay) {
            is CelebrationOverlay.Badge -> sounds.badge()
            is CelebrationOverlay.Streak -> sounds.streak()
            null -> Unit
        }
    }

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                if (perfect) "🏆" else "🎉",
                fontSize = 96.sp,
                modifier = Modifier.popIn(stage >= STAGE_TROPHY)
            )
            Spacer(Modifier.height(12.dp))
            Column(
                Modifier.popIn(stage >= STAGE_TITLE),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (perfect) "Perfekt!" else "Geschafft!",
                    style = MaterialTheme.typography.displaySmall,
                    color = AppColors.GreenDark,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (perfect) {
                        "Alles richtig – du bist ein Türkisch-Profi! 🌟"
                    } else {
                        "Weiter so! Übung macht den Meister."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(28.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultCard(
                    emoji = "⭐", label = "gesammelt", color = AppColors.Gold,
                    modifier = Modifier
                        .weight(1f)
                        .popIn(stage >= STAGE_XP)
                ) {
                    AnimatedCounter(
                        target = earnedXp,
                        start = stage >= STAGE_XP,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.Gold,
                        suffix = " XP",
                        durationMillis = COUNTER_MILLIS,
                        ticks = LessonLogic.xpTickCount(earnedXp),
                        onTick = { sounds.xpTick() }
                    )
                }
                ResultCard(
                    emoji = "🎯", label = "Genauigkeit", color = AppColors.Blue,
                    modifier = Modifier
                        .weight(1f)
                        .popIn(stage >= STAGE_ACCURACY)
                ) {
                    Text("$accuracyPercent %", style = MaterialTheme.typography.titleLarge, color = AppColors.Blue)
                }
                ResultCard(
                    emoji = "🔥", label = if (streakDays == 1) "Tag Serie" else "Tage Serie",
                    color = AppColors.Orange,
                    modifier = Modifier
                        .weight(1f)
                        .popIn(stage >= STAGE_STREAK)
                ) {
                    Text("$streakDays", style = MaterialTheme.typography.titleLarge, color = AppColors.Orange)
                }
            }

            Spacer(Modifier.height(36.dp))
            ChunkyButton(
                text = "WEITER",
                modifier = Modifier
                    .fillMaxWidth()
                    .popIn(stage >= STAGE_DONE),
                enabled = stage >= STAGE_DONE
            ) { onContinue() }
        }

        ConfettiOverlay()

        if (activeOverlay != null) {
            key(overlayIndex) {
                CelebrationOverlayView(activeOverlay) { overlayIndex++ }
            }
        }
    }
}

@Composable
private fun ResultCard(
    emoji: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, color, RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 26.sp)
        value()
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Vollbild-Einblendung für ein neues Abzeichen oder die verlängerte Serie. */
@Composable
private fun CelebrationOverlayView(overlay: CelebrationOverlay, onDismiss: () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val pulseModifier = Modifier.graphicsLayer {
        scaleX = pulse
        scaleY = pulse
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(32.dp)
                .popIn(shown)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (overlay) {
                is CelebrationOverlay.Badge -> {
                    Text("Neues Abzeichen!", style = MaterialTheme.typography.titleMedium, color = AppColors.Gold)
                    Spacer(Modifier.height(8.dp))
                    Text(overlay.achievement.emoji, fontSize = 80.sp, modifier = pulseModifier)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        overlay.achievement.title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        overlay.achievement.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                is CelebrationOverlay.Streak -> {
                    Text("🔥", fontSize = 96.sp, modifier = pulseModifier)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Serie verlängert!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppColors.Orange
                    )
                    Text(
                        if (overlay.days == 1) "1 Tag in Folge" else "${overlay.days} Tage in Folge",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Tippe, um weiterzumachen",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
