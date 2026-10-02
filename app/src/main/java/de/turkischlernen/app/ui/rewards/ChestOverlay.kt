package de.turkischlernen.app.ui.rewards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.progress.ChestReward
import de.turkischlernen.app.data.settings.AvatarConfig
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.components.ConfettiOverlay
import de.turkischlernen.app.ui.components.popIn
import de.turkischlernen.app.ui.mascot.Kangal
import de.turkischlernen.app.ui.mascot.MascotMood
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.delay

private const val VIEW = 100f

private val Wood = Color(0xFF8D5524)
private val WoodDark = Color(0xFF5E3815)
private val Gold = Color(0xFFFFC107)
private val GoldDark = Color(0xFFC79100)
private val Ray = Color(0xFFFFE082)

/**
 * Vollbild-Einblendung für eine Schatztruhe: Deckel springt auf, Lichtstrahlen
 * drehen sich, Konfetti fällt, der Gewinn wird angezeigt.
 */
@Composable
fun ChestOverlay(
    reward: ChestReward,
    avatar: AvatarConfig,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sounds = LocalAppContainer.current.sounds

    val lid = remember { Animatable(0f) }
    var showReward by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Kurz wackeln, dann aufspringen.
        delay(250)
        lid.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        sounds.badge()
        showReward = true
    }

    val shake by rememberInfiniteTransitionShake()
    val raySpin by rememberInfiniteTransitionSpin()

    Box(
        modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Schatztruhe!",
                style = MaterialTheme.typography.displaySmall,
                color = Gold
            )
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Kangal(mood = MascotMood.CHEER, size = 110.dp, avatar = avatar)
                Spacer(Modifier.width(4.dp))
                Canvas(Modifier.size(170.dp)) {
                    val factor = minOf(this.size.width, this.size.height) / VIEW
                    withTransform({ scale(factor, factor, pivot = Offset.Zero) }) {
                        drawChest(
                            lidOpen = lid.value,
                            wobble = if (lid.value < 0.05f) shake else 0f,
                            raySpin = raySpin
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(
                Modifier
                    .popIn(showReward)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (reward) {
                    is ChestReward.Xp -> {
                        Text("⭐", fontSize = 44.sp)
                        Text(
                            "+${reward.amount} XP",
                            style = MaterialTheme.typography.displaySmall,
                            color = AppColors.Gold
                        )
                    }

                    is ChestReward.Item -> {
                        Text("🎁", fontSize = 44.sp)
                        Text(
                            "Neu: ${reward.label}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = AppColors.GreenDark,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Im Profil anziehen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            ChunkyButton(
                text = "NEHMEN",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .popIn(showReward),
                enabled = showReward
            ) { onClose() }
        }

        if (showReward) ConfettiOverlay()
    }
}

@Composable
private fun rememberInfiniteTransitionShake() =
    rememberInfiniteTransition(label = "chestShake").animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(90, easing = LinearEasing), RepeatMode.Reverse),
        label = "shake"
    )

@Composable
private fun rememberInfiniteTransitionSpin() =
    rememberInfiniteTransition(label = "chestRays").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "spin"
    )

/** Zeichnet die Truhe: Lichtstrahlen, Kiste, aufklappender Deckel, Schloss. */
private fun DrawScope.drawChest(lidOpen: Float, wobble: Float, raySpin: Float) {
    if (lidOpen > 0.1f) {
        rotate(degrees = raySpin, pivot = Offset(50f, 46f)) {
            repeat(12) { index ->
                rotate(degrees = index * 30f, pivot = Offset(50f, 46f)) {
                    drawLine(
                        color = Ray.copy(alpha = 0.55f * lidOpen),
                        start = Offset(50f, 46f),
                        end = Offset(50f, -6f),
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }

    rotate(degrees = wobble * 3f, pivot = Offset(50f, 92f)) {
        // Deckel
        rotate(degrees = -38f * lidOpen, pivot = Offset(16f, 50f)) {
            drawRoundRect(
                color = Wood,
                topLeft = Offset(14f, 30f),
                size = Size(72f, 22f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            drawRoundRect(
                color = WoodDark,
                topLeft = Offset(14f, 30f),
                size = Size(72f, 22f),
                cornerRadius = CornerRadius(10f, 10f),
                style = Stroke(3f)
            )
            drawRect(color = Gold, topLeft = Offset(44f, 30f), size = Size(12f, 22f))
        }

        // Kiste
        drawRoundRect(
            color = Wood,
            topLeft = Offset(14f, 50f),
            size = Size(72f, 42f),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = WoodDark,
            topLeft = Offset(14f, 50f),
            size = Size(72f, 42f),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(3f)
        )
        drawRect(color = Gold, topLeft = Offset(44f, 50f), size = Size(12f, 42f))

        // Schloss
        drawRoundRect(
            color = GoldDark,
            topLeft = Offset(42f, 58f),
            size = Size(16f, 14f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawCircle(color = WoodDark, radius = 2.5f, center = Offset(50f, 65f))
    }
}
