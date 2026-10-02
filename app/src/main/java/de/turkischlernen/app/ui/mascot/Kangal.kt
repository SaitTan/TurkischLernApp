package de.turkischlernen.app.ui.mascot

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val Fur = Color(0xFFD9A066)
private val FurDark = Color(0xFFB9834B)
private val Mask = Color(0xFF3B2F2A)
private val Belly = Color(0xFFF2DCC0)
private val Tongue = Color(0xFFEF6E8C)
private val EyeWhite = Color(0xFFFFFFFF)

/**
 * Der Kangal – komplett gezeichnet, ohne Bilddateien. Jedes Körperteil bewegt
 * sich einzeln, damit die Stimmungen erkennbar sind.
 */
@Composable
fun Kangal(
    mood: MascotMood,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    val transition = rememberInfiniteTransition(label = "kangal")

    // Atmen: der ganze Körper wird minimal größer und kleiner.
    val breath by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "breath"
    )

    // Schwanz: Grundtakt immer gleich, die Stärke hängt an der Stimmung.
    val wagPhase by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
        label = "wag"
    )
    val wagAmount = when (mood) {
        MascotMood.HAPPY, MascotMood.CHEER -> 1f
        MascotMood.SAD -> 0f
        else -> 0.35f
    }

    // Blinzeln: Augen sind fast immer offen, kurz zu.
    val eyeOpen by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 3200
                1f at 0
                1f at 2900
                0.1f at 3020
                1f at 3140
            },
            RepeatMode.Restart
        ),
        label = "blink"
    )

    // Winken: kurze Pfotenbewegung alle paar Sekunden.
    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 2600
                0f at 0
                0f at 1800
                1f at 1950
                0f at 2100
                1f at 2250
                0f at 2400
            },
            RepeatMode.Restart
        ),
        label = "wave"
    )

    // Sprung bei Erfolg bzw. Jubel.
    val hop = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        when (mood) {
            MascotMood.HAPPY -> {
                hop.animateTo(
                    1f,
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh)
                )
                hop.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }

            MascotMood.CHEER -> repeat(3) {
                hop.animateTo(1f, tween(220, easing = FastOutSlowInEasing))
                hop.animateTo(0f, tween(220, easing = FastOutSlowInEasing))
            }

            else -> hop.snapTo(0f)
        }
    }

    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val lift = hop.value * h * 0.10f

        translate(top = -lift) {
            drawBody(w, h, breath, wagPhase * wagAmount, mood)
            drawHead(w, h, eyeOpen, mood)
            if (mood == MascotMood.WAVE) drawWavingPaw(w, h, wavePhase)
        }
    }
}

/** Körper, Beine und Schwanz. */
private fun DrawScope.drawBody(w: Float, h: Float, breath: Float, wag: Float, mood: MascotMood) {
    // Schwanz hinter dem Körper, wedelt um seinen Ansatz.
    rotate(degrees = wag * 22f, pivot = Offset(w * 0.26f, h * 0.70f)) {
        drawOval(
            color = FurDark,
            topLeft = Offset(w * 0.08f, h * 0.60f),
            size = Size(w * 0.22f, h * 0.12f)
        )
    }

    // Vorderbeine
    drawOval(FurDark, Offset(w * 0.34f, h * 0.84f), Size(w * 0.12f, h * 0.14f))
    drawOval(FurDark, Offset(w * 0.54f, h * 0.84f), Size(w * 0.12f, h * 0.14f))

    // Rumpf (atmet)
    val bodyW = w * 0.56f * breath
    val bodyH = h * 0.42f * breath
    drawOval(
        color = Fur,
        topLeft = Offset(w * 0.5f - bodyW / 2f, h * 0.92f - bodyH),
        size = Size(bodyW, bodyH)
    )

    // Heller Brustfleck
    drawOval(
        color = Belly,
        topLeft = Offset(w * 0.42f, h * 0.68f),
        size = Size(w * 0.16f, h * 0.20f)
    )

    // Bei Traurigkeit sitzt der Hund etwas zusammengesunken.
    if (mood == MascotMood.SAD) {
        drawOval(
            color = FurDark,
            topLeft = Offset(w * 0.30f, h * 0.88f),
            size = Size(w * 0.40f, h * 0.06f)
        )
    }
}

/** Kopf mit Ohren, Maske, Augen, Schnauze. */
private fun DrawScope.drawHead(w: Float, h: Float, eyeOpen: Float, mood: MascotMood) {
    val headDrop = if (mood == MascotMood.SAD) h * 0.05f else 0f
    val cx = w * 0.5f
    val cy = h * 0.36f + headDrop
    val r = w * 0.26f

    // Schlappohren – bei Traurigkeit weiter unten.
    val earDrop = if (mood == MascotMood.SAD) h * 0.06f else 0f
    drawOval(
        color = FurDark,
        topLeft = Offset(cx - r * 1.25f, cy - r * 0.55f + earDrop),
        size = Size(r * 0.55f, r * 1.15f)
    )
    drawOval(
        color = FurDark,
        topLeft = Offset(cx + r * 0.70f, cy - r * 0.55f + earDrop),
        size = Size(r * 0.55f, r * 1.15f)
    )

    // Kopf
    drawCircle(color = Fur, radius = r, center = Offset(cx, cy))

    // Schwarze Maske um Schnauze und Augenpartie
    drawOval(
        color = Mask,
        topLeft = Offset(cx - r * 0.62f, cy - r * 0.10f),
        size = Size(r * 1.24f, r * 1.00f)
    )

    // Augen: offen als Kreis, beim Jubeln als lachende Bögen.
    val eyeY = cy - r * 0.22f
    val eyeDx = r * 0.38f
    if (mood == MascotMood.CHEER) {
        listOf(cx - eyeDx, cx + eyeDx).forEach { ex ->
            drawArc(
                color = EyeWhite,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(ex - r * 0.18f, eyeY - r * 0.16f),
                size = Size(r * 0.36f, r * 0.32f),
                style = Stroke(width = r * 0.10f)
            )
        }
    } else {
        listOf(cx - eyeDx, cx + eyeDx).forEach { ex ->
            drawOval(
                color = EyeWhite,
                topLeft = Offset(ex - r * 0.14f, eyeY - r * 0.14f * eyeOpen),
                size = Size(r * 0.28f, r * 0.28f * eyeOpen)
            )
            drawCircle(
                color = Mask,
                radius = r * 0.08f * eyeOpen,
                center = Offset(ex, eyeY)
            )
        }
    }

    // Nase
    drawOval(
        color = Mask,
        topLeft = Offset(cx - r * 0.14f, cy + r * 0.28f),
        size = Size(r * 0.28f, r * 0.20f)
    )

    // Mund: lacht, außer bei Traurigkeit.
    val mouthY = cy + r * 0.52f
    drawArc(
        color = Mask,
        startAngle = if (mood == MascotMood.SAD) 200f else 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(cx - r * 0.26f, if (mood == MascotMood.SAD) mouthY else mouthY - r * 0.20f),
        size = Size(r * 0.52f, r * 0.30f),
        style = Stroke(width = r * 0.07f)
    )

    // Zunge beim Freuen
    if (mood == MascotMood.HAPPY || mood == MascotMood.CHEER) {
        drawOval(
            color = Tongue,
            topLeft = Offset(cx - r * 0.12f, mouthY + r * 0.02f),
            size = Size(r * 0.24f, r * 0.26f)
        )
    }
}

/** Erhobene, winkende Pfote. */
private fun DrawScope.drawWavingPaw(w: Float, h: Float, wave: Float) {
    val pivot = Offset(w * 0.70f, h * 0.68f)
    rotate(degrees = -20f - wave * 25f, pivot = pivot) {
        drawOval(
            color = Fur,
            topLeft = Offset(w * 0.68f, h * 0.50f),
            size = Size(w * 0.14f, h * 0.22f)
        )
        drawCircle(color = Belly, radius = w * 0.055f, center = Offset(w * 0.75f, h * 0.52f))
    }
}
