package de.turkischlernen.app.ui.avatar

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.turkischlernen.app.data.settings.AvatarConfig
import de.turkischlernen.app.ui.components.darker

/*
 * Die eigene Figur des Kindes – als Brustbild gezeichnet, so wie ein Profilbild.
 * Alles entsteht aus Kreisen, Ovalen und Bögen; nichts wird aus einem Foto abgeleitet.
 * Koordinatensystem: 200 × 220, danach auf die gewünschte Größe skaliert.
 */

private const val VIEW_W = 200f
private const val VIEW_H = 220f

private val Outline = Color(0xFF3F3330)
private val EyeWhite = Color(0xFFFFFFFF)
private val Pupil = Color(0xFF221A16)
private val MouthColor = Color(0xFF7A4038)
private val GlassFrame = Color(0xFF2F2F33)

/**
 * Zeichnet die Figur aus [avatar]: Hautton, Frisur, Haarfarbe, T-Shirt und Brille.
 * Blinzelt gelegentlich, damit sie lebendig wirkt.
 */
@Composable
fun HumanAvatar(
    avatar: AvatarConfig,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    /** true = nur der Kopf, herangezoomt (für kleine Vorschau-Kacheln). */
    headOnly: Boolean = false
) {
    val blink by rememberInfiniteTransition(label = "avatar").animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 5200
                1f at 0
                1f at 4900
                0.08f at 5020
                1f at 5140
            },
            RepeatMode.Restart
        ),
        label = "blink"
    )

    val skin = Color(avatar.skin.hex)
    val hair = Color(avatar.hairColor.hex)
    val shirt = Color(avatar.shirt.hex)

    Canvas(modifier.size(size)) {
        val factor = minOf(this.size.width / VIEW_W, this.size.height / VIEW_H)
        val offsetX = (this.size.width - VIEW_W * factor) / 2f

        withTransform({
            translate(left = offsetX)
            scale(factor, factor, pivot = Offset.Zero)
        }) {
            if (headOnly) {
                withTransform({
                    scale(1.55f, 1.55f, pivot = Offset(100f, 96f))
                    translate(top = 22f)
                }) {
                    drawHeadAndFace(skin, hair, avatar.hairId, blink, avatar.glasses)
                }
            } else {
                drawShirt(shirt)
                drawHeadAndFace(skin, hair, avatar.hairId, blink, avatar.glasses)
            }
        }
    }
}

/** Schmale Schultern mit T-Shirt, kurzen Ärmeln und Kragen. */
private fun DrawScope.drawShirt(shirt: Color) {
    val dark = shirt.darker(0.84f)

    // Kurze Ärmel
    listOf(38f, 134f).forEach { left ->
        drawRoundRect(
            color = dark,
            topLeft = Offset(left, 172f),
            size = Size(28f, 36f),
            cornerRadius = CornerRadius(13f, 13f)
        )
        drawRoundRect(
            color = Outline,
            topLeft = Offset(left, 172f),
            size = Size(28f, 36f),
            cornerRadius = CornerRadius(13f, 13f),
            style = Stroke(2.5f)
        )
    }

    // Oberkörper – schmaler als die Schultern, läuft nach unten gerade aus
    drawRoundRect(
        color = shirt,
        topLeft = Offset(56f, 166f),
        size = Size(88f, 62f),
        cornerRadius = CornerRadius(24f, 24f)
    )
    drawRoundRect(
        color = Outline,
        topLeft = Offset(56f, 166f),
        size = Size(88f, 62f),
        cornerRadius = CornerRadius(24f, 24f),
        style = Stroke(2.5f)
    )

    // Kragen
    drawArc(
        color = dark,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(84f, 158f),
        size = Size(32f, 24f),
        style = Stroke(5f, cap = StrokeCap.Round)
    )
}

/** Kopf, Haare, Augen, Nase, Mund und optional die Brille. */
private fun DrawScope.drawHeadAndFace(
    skin: Color,
    hair: Color,
    hairId: String,
    blink: Float,
    glasses: Boolean
) {
    val skinShadow = skin.darker(0.9f)

    // Hals – schmal, verschwindet im Kragen
    drawRoundRect(
        color = skinShadow,
        topLeft = Offset(90f, 124f),
        size = Size(20f, 44f),
        cornerRadius = CornerRadius(9f, 9f)
    )

    // Ohren
    drawCircle(skin, radius = 10f, center = Offset(56f, 96f))
    drawCircle(Outline, radius = 10f, center = Offset(56f, 96f), style = Stroke(2.5f))
    drawCircle(skin, radius = 10f, center = Offset(144f, 96f))
    drawCircle(Outline, radius = 10f, center = Offset(144f, 96f), style = Stroke(2.5f))

    // Haar-Grundform (liegt hinter dem Gesicht und bildet den Rand)
    when (hairId) {
        "undercut" -> drawOval(hair, Offset(64f, 36f), Size(72f, 72f))
        else -> drawOval(hair, Offset(54f, 36f), Size(92f, 80f))
    }

    // Gesicht
    drawOval(skin, Offset(58f, 50f), Size(84f, 92f))
    drawOval(Outline, Offset(58f, 50f), Size(84f, 92f), style = Stroke(2.5f))

    // Frisur-Details über dem Gesicht
    when (hairId) {
        "kurz" -> {
            drawOval(hair, Offset(58f, 44f), Size(84f, 36f))
            drawArc(
                color = hair,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(58f, 46f),
                size = Size(84f, 50f)
            )
        }

        "locken" -> {
            listOf(
                Offset(70f, 56f), Offset(86f, 46f), Offset(100f, 42f),
                Offset(114f, 46f), Offset(130f, 56f), Offset(62f, 72f), Offset(138f, 72f)
            ).forEach { drawCircle(hair, radius = 15f, center = it) }
        }

        "pony" -> {
            drawArc(
                color = hair,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(56f, 42f),
                size = Size(88f, 56f)
            )
            // Fransen über der Stirn
            drawOval(hair, Offset(58f, 62f), Size(84f, 26f))
        }

        "undercut" -> {
            drawArc(
                color = hair,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(64f, 38f),
                size = Size(72f, 44f)
            )
            // kurz rasierte Seiten
            drawOval(hair.copy(alpha = 0.35f), Offset(58f, 66f), Size(16f, 30f))
            drawOval(hair.copy(alpha = 0.35f), Offset(126f, 66f), Size(16f, 30f))
        }
    }

    // Augenbrauen
    drawLine(
        color = hair,
        start = Offset(74f, 92f),
        end = Offset(92f, 89f),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = hair,
        start = Offset(108f, 89f),
        end = Offset(126f, 92f),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )

    // Augen
    listOf(83f, 117f).forEach { cx ->
        scale(scaleX = 1f, scaleY = blink, pivot = Offset(cx, 102f)) {
            drawOval(EyeWhite, Offset(cx - 10f, 95f), Size(20f, 14f))
            drawOval(Outline, Offset(cx - 10f, 95f), Size(20f, 14f), style = Stroke(1.6f))
            drawCircle(Pupil, radius = 5f, center = Offset(cx, 102f))
            drawCircle(EyeWhite, radius = 1.8f, center = Offset(cx + 2f, 100f))
        }
    }

    // Nase
    drawArc(
        color = skinShadow,
        startAngle = 300f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(94f, 104f),
        size = Size(12f, 16f),
        style = Stroke(2.5f, cap = StrokeCap.Round)
    )

    // Lächeln
    drawArc(
        color = MouthColor,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(86f, 112f),
        size = Size(28f, 18f),
        style = Stroke(3f, cap = StrokeCap.Round)
    )

    if (glasses) drawGlasses()
}

/** Schlichte Brille über den Augen. */
private fun DrawScope.drawGlasses() {
    listOf(70f, 104f).forEach { left ->
        drawRoundRect(
            color = GlassFrame,
            topLeft = Offset(left, 92f),
            size = Size(26f, 20f),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(3f)
        )
    }
    drawLine(
        color = GlassFrame,
        start = Offset(96f, 101f),
        end = Offset(104f, 101f),
        strokeWidth = 3f
    )
    drawLine(
        color = GlassFrame,
        start = Offset(70f, 99f),
        end = Offset(58f, 97f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = GlassFrame,
        start = Offset(130f, 99f),
        end = Offset(142f, 97f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
}
