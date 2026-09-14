package de.turkischlernen.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import de.turkischlernen.app.ui.theme.AppColors
import kotlin.random.Random

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val sizePx: Float,
    val spin: Float
)

/**
 * Konfetti-Regen bei richtigen Antworten und geschafften Lektionen.
 * Komplett selbst gezeichnet – keine zusätzliche Bibliothek nötig.
 */
@Composable
fun ConfettiOverlay(
    modifier: Modifier = Modifier,
    particleCount: Int = 70,
    durationMillis: Int = 2200
) {
    val colors = listOf(
        AppColors.Green, AppColors.Blue, AppColors.Gold,
        AppColors.Orange, AppColors.Purple, AppColors.Red
    )
    val particles = remember {
        val random = Random(System.currentTimeMillis())
        List(particleCount) {
            ConfettiParticle(
                startX = random.nextFloat(),
                startY = -0.1f - random.nextFloat() * 0.3f,
                velocityX = (random.nextFloat() - 0.5f) * 0.5f,
                velocityY = 0.5f + random.nextFloat() * 0.7f,
                color = colors[random.nextInt(colors.size)],
                sizePx = 14f + random.nextFloat() * 18f,
                spin = (random.nextFloat() - 0.5f) * 900f
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(durationMillis, easing = LinearEasing))
    }

    Canvas(modifier.fillMaxSize()) {
        val t = progress.value
        particles.forEach { p ->
            val x = (p.startX + p.velocityX * t) * size.width
            val y = (p.startY + p.velocityY * t + 0.9f * t * t) * size.height
            val alpha = (1f - t * t).coerceIn(0f, 1f)
            rotate(degrees = p.spin * t, pivot = Offset(x, y)) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(x - p.sizePx / 2f, y - p.sizePx / 2f),
                    size = Size(p.sizePx, p.sizePx * 0.55f),
                    alpha = alpha
                )
            }
        }
    }
}
