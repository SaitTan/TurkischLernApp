package de.turkischlernen.app.ui.mascot

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.turkischlernen.app.data.settings.AvatarConfig
import de.turkischlernen.app.ui.components.darker

/*
 * Der Sivas-Kangal nach einer SVG-Vorlage: die Pfaddaten sind unverändert
 * übernommen, die CSS-Animationen sind in Compose nachgebaut. Gezeichnet wird im
 * Koordinatensystem 200 × 220 (viewBox) und auf die gewünschte Größe skaliert.
 * Fellfarbe und Accessoire kommen aus dem Avatar des Kindes.
 */

private const val VIEW_W = 200f
private const val VIEW_H = 220f

private val Line = Color(0xFF5B3D28)
private val MaskColor = Color(0xFF4A3A33)
private val Ink = Color(0xFF140D0A)
private val Nose = Color(0xFF1C1411)
private val TongueColor = Color(0xFFF27A8A)
private val TongueLine = Color(0xFFB9475A)
private val Blush = Color(0xFFF2998C)
private val ShadowColor = Color(0x2E462D14)
private val MouthInside = Color(0xFF2A100E)
private val SparkleGold = Color(0xFFFFC531)
private val SparkleGoldLine = Color(0xFFE39A00)
private val SparkleBlue = Color(0xFF5EC8F2)
private val SparkleBlueLine = Color(0xFF2C93BD)
private val SparklePink = Color(0xFFF27A8A)
private val SparklePinkLine = Color(0xFFB9475A)

private val CapColor = Color(0xFFE53935)
private val CapBrim = Color(0xFFB71C1C)
private val GlassColor = Color(0xFF26262B)
private val BowColor = Color(0xFFEC407A)
private val BowDark = Color(0xFFC2185B)
private val ScarfColor = Color(0xFFD32F2F)
private val ScarfStripe = Color(0xFFFFFFFF)

/** Die vier zusammengehörenden Farbtöne eines Fells. */
private data class KangalPalette(
    val fur: Color,
    val furDark: Color,
    val cream: Color,
    val ear: Color
)

private fun svgPath(data: String): Path = PathParser().parsePathString(data).toPath()

// --- Pfade aus der SVG-Vorlage -------------------------------------------------

private val TailPath by lazy {
    svgPath(
        "M132 178 C 162 176, 177 150, 167 130 C 160 116, 143 120, 146 133 " +
            "C 148 141, 157 140, 158 134"
    )
}
private val TorsoPath by lazy { svgPath("M66 197 C 57 165, 66 122, 100 116 C 134 122, 143 165, 134 197 Z") }
private val ChestPath by lazy {
    svgPath("M86 128 C 92 141, 108 141, 114 128 C 117 152, 111 176, 100 184 C 89 176, 83 152, 86 128 Z")
}
private val HipLeftPath by lazy { svgPath("M58 185 C 62 180, 70 180, 74 186") }
private val HipRightPath by lazy { svgPath("M142 185 C 138 180, 130 180, 126 186") }
private val HeadPath by lazy {
    svgPath(
        "M100 44 C 128 44, 140 62, 139 82 C 138 104, 122 118, 100 118 " +
            "C 78 118, 62 104, 61 82 C 60 62, 72 44, 100 44 Z"
    )
}
private val BlazePath by lazy { svgPath("M100 50 C 94 56, 93 66, 96 76 L 104 76 C 107 66, 106 56, 100 50 Z") }
private val EarLeftPath by lazy {
    svgPath("M71 55 C 56 57, 49 82, 54 101 C 57 110, 70 107, 73 95 C 76 82, 77 66, 71 55 Z")
}
private val EarRightPath by lazy {
    svgPath("M129 55 C 144 57, 151 82, 146 101 C 143 110, 130 107, 127 95 C 124 82, 123 66, 129 55 Z")
}
private val MuzzlePath by lazy {
    svgPath(
        "M100 84 C 116 84, 126 94, 124 105 C 122 115, 112 121, 100 121 " +
            "C 88 121, 78 115, 76 105 C 74 94, 84 84, 100 84 Z"
    )
}
private val NosePath by lazy {
    svgPath("M93 92 C 93 87.5, 107 87.5, 107 92 C 107 97, 102 99.5, 100 99.5 C 98 99.5, 93 97, 93 92 Z")
}
private val MouthPath by lazy { svgPath("M100 99.5 v4 M92 104 Q 96 108.5 100 103.5 Q 104 108.5 108 104") }
private val MouthSadPath by lazy { svgPath("M100 99.5 v4.5 M93 109 Q 100 103 107 109") }
private val MouthOpenPath by lazy { svgPath("M89 103 Q 100 121 111 103 Q 100 106 89 103 Z") }
private val TonguePath by lazy { svgPath("M94.5 105 C 94 117, 106 117, 105.5 105 Z") }
private val TongueLinePath by lazy { svgPath("M100 106.5 v6") }
private val BrowLeftPath by lazy { svgPath("M78 71 L 91 66") }
private val BrowRightPath by lazy { svgPath("M122 71 L 109 66") }
private val EyeHappyLeftPath by lazy { svgPath("M79 81 Q 86 71 93 81") }
private val EyeHappyRightPath by lazy { svgPath("M107 81 Q 114 71 121 81") }
private val SparkleBigPath by lazy { svgPath("M30 60 l4 9 9 4 -9 4 -4 9 -4 -9 -9 -4 9 -4z") }
private val SparkleSmall2Path by lazy { svgPath("M170 50 l3 7 7 3 -7 3 -3 7 -3 -7 -7 -3 7 -3z") }
private val SparkleSmall3Path by lazy { svgPath("M176 128 l3 6 6 3 -6 3 -3 6 -3 -6 -6 -3 6 -3z") }
private val SparkleSmall4Path by lazy { svgPath("M22 134 l3 6 6 3 -6 3 -3 6 -3 -6 -6 -3 6 -3z") }
private val CapDomePath by lazy { svgPath("M63 55 C 66 29, 134 29, 137 55 Z") }
private val BowLeftPath by lazy { svgPath("M56 44 L70 52 L56 60 Z") }
private val BowRightPath by lazy { svgPath("M84 44 L70 52 L84 60 Z") }

/**
 * Der Kangal. [mood] bestimmt Haltung und Bewegung, [avatar] Fell und Accessoire,
 * [size] die Kantenlänge. Setzt man dieselbe Stimmung erneut, läuft der Hüpfer erneut.
 */
@Composable
fun Kangal(
    mood: MascotMood,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    avatar: AvatarConfig = AvatarConfig()
) {
    val palette = remember(avatar.furId) {
        val fur = avatar.fur
        KangalPalette(
            fur = Color(fur.furHex),
            furDark = Color(fur.furDarkHex),
            cream = Color(fur.creamHex),
            ear = Color(fur.furDarkHex).darker(0.62f)
        )
    }

    val transition = rememberInfiniteTransition(label = "kangal")

    val breath by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(if (mood == MascotMood.HAPPY) 1400 else 3200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "breath"
    )

    val wagPhase by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(
                when (mood) {
                    MascotMood.HAPPY -> 260
                    MascotMood.CHEER -> 220
                    MascotMood.WAVE -> 700
                    else -> 1000
                },
                easing = LinearEasing
            ),
            RepeatMode.Reverse
        ),
        label = "wag"
    )

    val eyeOpen by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 4600
                1f at 0
                1f at 4280
                0.08f at 4400
                1f at 4520
            },
            RepeatMode.Restart
        ),
        label = "blink"
    )

    val earFlap by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ears"
    )

    val headIdle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 6400
                0f at 0
                0f at 2560
                1f at 3520
                1f at 4800
                0f at 6400
            },
            RepeatMode.Restart
        ),
        label = "headIdle"
    )

    // Winken: Vorderpfote geht hoch, wedelt dreimal, geht wieder runter.
    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 4000
                0f at 0
                1f at 400
                0.75f at 680
                1.05f at 960
                0.75f at 1240
                1.05f at 1520
                0.8f at 1800
                0f at 2200
                0f at 4000
            },
            RepeatMode.Restart
        ),
        label = "wave"
    )

    val sparkle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sparkle"
    )

    // Hüpfer: einmal bei HAPPY, dauerhaft bei CHEER.
    val hop = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        when (mood) {
            MascotMood.HAPPY -> {
                hop.snapTo(0f)
                hop.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
                hop.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
            }

            MascotMood.CHEER -> while (true) {
                hop.animateTo(1f, tween(430, easing = FastOutSlowInEasing))
                hop.animateTo(0f, tween(470, easing = FastOutSlowInEasing))
            }

            else -> hop.snapTo(0f)
        }
    }

    Canvas(modifier.size(size)) {
        val factor = minOf(this.size.width / VIEW_W, this.size.height / VIEW_H)
        val offsetX = (this.size.width - VIEW_W * factor) / 2f

        withTransform({
            translate(left = offsetX)
            scale(factor, factor, pivot = Offset.Zero)
        }) {
            drawKangal(
                mood = mood,
                palette = palette,
                accessoryId = avatar.accessoryId,
                breath = breath,
                wag = wagPhase,
                eyeOpen = eyeOpen,
                earFlap = earFlap,
                headIdle = headIdle,
                wave = wavePhase,
                sparkle = sparkle,
                hop = hop.value
            )
        }
    }
}

private fun DrawScope.drawKangal(
    mood: MascotMood,
    palette: KangalPalette,
    accessoryId: String,
    breath: Float,
    wag: Float,
    eyeOpen: Float,
    earFlap: Float,
    headIdle: Float,
    wave: Float,
    sparkle: Float,
    hop: Float
) {
    val jumping = mood == MascotMood.CHEER
    val lift = if (jumping) hop * 42f else hop * 34f
    val squash = 1f + 0.06f * (1f - hop) * (if (hop > 0f) 1f else 0f)

    // Schatten wird kleiner, wenn der Hund in der Luft ist.
    val shadowScale = 1f - 0.4f * hop
    scale(scaleX = shadowScale, scaleY = shadowScale, pivot = Offset(100f, 207f)) {
        drawOval(color = ShadowColor, topLeft = Offset(46f, 200f), size = Size(108f, 14f))
    }

    if (jumping) drawSparkles(sparkle)

    translate(top = -lift) {
        scale(scaleX = squash, scaleY = 2f - squash, pivot = Offset(100f, 206f)) {
            drawTail(mood, palette, wag)
            drawTorso(mood, palette, breath)
            drawLegs(mood, palette, wave, earFlap)
            drawHead(mood, palette, accessoryId, eyeOpen, earFlap, headIdle, wave)
        }
    }
}

/** Buschiger Schwanz: dunkle Kontur, hellere Füllung darüber. */
private fun DrawScope.drawTail(mood: MascotMood, palette: KangalPalette, wag: Float) {
    val degrees = when (mood) {
        MascotMood.SAD -> 72f
        MascotMood.HAPPY, MascotMood.CHEER -> wag * 16f
        else -> wag * 8f
    }
    rotate(degrees = degrees, pivot = Offset(133f, 178f)) {
        drawPath(TailPath, color = Line, style = Stroke(17f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(
            TailPath,
            color = palette.fur,
            style = Stroke(11.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/** Rumpf mit Brustfleck, Hüften und Hinterpfoten. */
private fun DrawScope.drawTorso(mood: MascotMood, palette: KangalPalette, breath: Float) {
    val amount = if (mood == MascotMood.SAD) 0.5f else 1f
    scale(
        scaleX = 1f + 0.018f * breath * amount,
        scaleY = 1f + 0.035f * breath * amount,
        pivot = Offset(100f, 200f)
    ) {
        drawPath(TorsoPath, color = palette.fur)
        drawPath(TorsoPath, color = Line, style = Stroke(2.5f, join = StrokeJoin.Round))
        drawPath(ChestPath, color = palette.cream)

        drawOval(palette.fur, Offset(48f, 173f), Size(40f, 30f))
        drawOval(palette.fur, Offset(112f, 173f), Size(40f, 30f))
        drawPath(HipLeftPath, color = palette.furDark, style = Stroke(2f))
        drawPath(HipRightPath, color = palette.furDark, style = Stroke(2f))
        drawOval(palette.cream, Offset(43f, 194.5f), Size(26f, 13f))
        drawOval(palette.cream, Offset(131f, 194.5f), Size(26f, 13f))
    }
}

/**
 * Vorderbeine. Beim Jubeln werden sie zu erhobenen Pfoten, beim Winken
 * hebt sich die rechte Pfote.
 */
private fun DrawScope.drawLegs(mood: MascotMood, palette: KangalPalette, wave: Float, flap: Float) {
    val leftDegrees = if (mood == MascotMood.CHEER) 118f + 24f * flap else 0f
    val rightDegrees = when (mood) {
        MascotMood.CHEER -> -(118f + 24f * (1f - flap))
        MascotMood.WAVE -> -138f * wave
        else -> 0f
    }

    rotate(degrees = leftDegrees, pivot = Offset(88f, 150f)) { drawLeg(palette, 80f, 88f) }
    rotate(degrees = rightDegrees, pivot = Offset(112f, 150f)) { drawLeg(palette, 104f, 112f) }
}

private fun DrawScope.drawLeg(palette: KangalPalette, left: Float, pawCenterX: Float) {
    drawRoundRect(
        color = palette.fur,
        topLeft = Offset(left, 144f),
        size = Size(16f, 54f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = Line,
        topLeft = Offset(left, 144f),
        size = Size(16f, 54f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(2.5f, join = StrokeJoin.Round)
    )
    drawOval(palette.cream, Offset(pawCenterX - 11.5f, 192f), Size(23f, 14f))
    drawOval(
        color = Line,
        topLeft = Offset(pawCenterX - 11.5f, 192f),
        size = Size(23f, 14f),
        style = Stroke(2.5f)
    )
}

/** Kopf mit Ohren, Maske, Augen, Nase, Mund und Accessoire. */
private fun DrawScope.drawHead(
    mood: MascotMood,
    palette: KangalPalette,
    accessoryId: String,
    eyeOpen: Float,
    flap: Float,
    headIdle: Float,
    wave: Float
) {
    val headRotation = when (mood) {
        MascotMood.SAD -> 5f
        MascotMood.IDLE -> -4f * headIdle
        MascotMood.WAVE -> -6f * wave
        else -> 0f
    }
    val headDrop = if (mood == MascotMood.SAD) 8f else 0f

    translate(top = headDrop) {
        rotate(degrees = headRotation, pivot = Offset(100f, 116f)) {
            if (accessoryId == "schal") drawScarf()

            drawEars(mood, palette, flap)

            drawPath(HeadPath, color = palette.fur)
            drawPath(HeadPath, color = Line, style = Stroke(2.5f))
            drawPath(BlazePath, color = palette.cream.copy(alpha = 0.55f))

            // Dunklere Augenpartie
            drawOval(MaskColor.copy(alpha = 0.28f), Offset(74f, 69.5f), Size(22f, 19f))
            drawOval(MaskColor.copy(alpha = 0.28f), Offset(104f, 69.5f), Size(22f, 19f))

            // Schnauzen-Maske
            drawPath(MuzzlePath, color = MaskColor)
            drawPath(MuzzlePath, color = Line, style = Stroke(2.5f))

            if (mood != MascotMood.SAD) {
                drawOval(Blush.copy(alpha = 0.55f), Offset(66f, 90.5f), Size(12f, 7f))
                drawOval(Blush.copy(alpha = 0.55f), Offset(122f, 90.5f), Size(12f, 7f))
            }

            if (mood == MascotMood.SAD) {
                drawPath(BrowLeftPath, color = Line, style = Stroke(2.6f, cap = StrokeCap.Round))
                drawPath(BrowRightPath, color = Line, style = Stroke(2.6f, cap = StrokeCap.Round))
            }

            drawEyes(mood, eyeOpen)

            drawPath(NosePath, color = Nose)
            drawOval(Color.White.copy(alpha = 0.55f), Offset(94.4f, 89.6f), Size(5.2f, 2.4f))

            drawMouth(mood)

            when (accessoryId) {
                "muetze" -> drawCap()
                "brille" -> drawGlasses()
                "schleife" -> drawBow()
            }
        }
    }
}

private fun DrawScope.drawEars(mood: MascotMood, palette: KangalPalette, flap: Float) {
    val flapping = mood == MascotMood.HAPPY || mood == MascotMood.CHEER
    val extra = if (flapping) 20f * flap else 0f
    val sadDrop = if (mood == MascotMood.SAD) 5f else 0f
    val sadTilt = if (mood == MascotMood.SAD) 7f else 0f

    translate(top = sadDrop) {
        rotate(degrees = -sadTilt + extra, pivot = Offset(71f, 57f)) {
            drawPath(EarLeftPath, color = palette.ear)
            drawPath(EarLeftPath, color = Line, style = Stroke(2.5f, join = StrokeJoin.Round))
        }
        rotate(degrees = sadTilt - extra, pivot = Offset(129f, 57f)) {
            drawPath(EarRightPath, color = palette.ear)
            drawPath(EarRightPath, color = Line, style = Stroke(2.5f, join = StrokeJoin.Round))
        }
    }
}

private fun DrawScope.drawEyes(mood: MascotMood, eyeOpen: Float) {
    if (mood == MascotMood.CHEER) {
        drawPath(EyeHappyLeftPath, color = Ink, style = Stroke(3.6f, cap = StrokeCap.Round))
        drawPath(EyeHappyRightPath, color = Ink, style = Stroke(3.6f, cap = StrokeCap.Round))
        return
    }

    val lidDrop = if (mood == MascotMood.SAD) 2.5f else 0f
    scale(scaleX = 1f, scaleY = eyeOpen, pivot = Offset(100f, 79f)) {
        translate(top = lidDrop) {
            listOf(86f, 114f).forEach { cx ->
                drawCircle(Ink, radius = 6.2f, center = Offset(cx, 79f))
                drawCircle(Color.White, radius = 2.2f, center = Offset(cx + 2.2f, 76.8f))
                drawCircle(Color.White.copy(alpha = 0.8f), radius = 1f, center = Offset(cx - 1.8f, 81.4f))
            }
        }
    }
}

private fun DrawScope.drawMouth(mood: MascotMood) {
    when (mood) {
        MascotMood.SAD ->
            drawPath(MouthSadPath, color = Ink, style = Stroke(2f, cap = StrokeCap.Round))

        MascotMood.HAPPY -> {
            drawPath(MouthPath, color = Ink, style = Stroke(2f, cap = StrokeCap.Round))
            drawTongue()
        }

        MascotMood.CHEER -> {
            drawPath(MouthOpenPath, color = MouthInside)
            drawPath(MouthOpenPath, color = Ink, style = Stroke(1.6f, join = StrokeJoin.Round))
            drawTongue()
        }

        else -> drawPath(MouthPath, color = Ink, style = Stroke(2f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawTongue() {
    drawPath(TonguePath, color = TongueColor)
    drawPath(TonguePath, color = TongueLine, style = Stroke(1.4f, join = StrokeJoin.Round))
    drawPath(TongueLinePath, color = TongueLine, style = Stroke(1.2f, cap = StrokeCap.Round))
}

// --- Accessoires ---------------------------------------------------------------

/** Baseball-Mütze auf dem Kopf. */
private fun DrawScope.drawCap() {
    drawPath(CapDomePath, color = CapColor)
    drawPath(CapDomePath, color = Line, style = Stroke(2.5f, join = StrokeJoin.Round))
    drawRoundRect(
        color = CapBrim,
        topLeft = Offset(56f, 50f),
        size = Size(88f, 11f),
        cornerRadius = CornerRadius(5.5f, 5.5f)
    )
    drawRoundRect(
        color = Line,
        topLeft = Offset(56f, 50f),
        size = Size(88f, 11f),
        cornerRadius = CornerRadius(5.5f, 5.5f),
        style = Stroke(2f)
    )
    drawCircle(color = CapBrim, radius = 5f, center = Offset(100f, 30f))
}

/** Sonnenbrille über den Augen. */
private fun DrawScope.drawGlasses() {
    listOf(72f, 100f).forEach { left ->
        drawRoundRect(
            color = GlassColor,
            topLeft = Offset(left, 69f),
            size = Size(28f, 20f),
            cornerRadius = CornerRadius(9f, 9f)
        )
        drawRoundRect(
            color = Line,
            topLeft = Offset(left, 69f),
            size = Size(28f, 20f),
            cornerRadius = CornerRadius(9f, 9f),
            style = Stroke(2f)
        )
        // Lichtreflex
        drawLine(
            color = Color.White.copy(alpha = 0.45f),
            start = Offset(left + 6f, 84f),
            end = Offset(left + 17f, 73f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )
    }
    drawRect(color = GlassColor, topLeft = Offset(98f, 75f), size = Size(4f, 5f))
}

/** Schleife am linken Ohr. */
private fun DrawScope.drawBow() {
    drawPath(BowLeftPath, color = BowColor)
    drawPath(BowLeftPath, color = BowDark, style = Stroke(2f, join = StrokeJoin.Round))
    drawPath(BowRightPath, color = BowColor)
    drawPath(BowRightPath, color = BowDark, style = Stroke(2f, join = StrokeJoin.Round))
    drawCircle(color = BowDark, radius = 4.5f, center = Offset(70f, 52f))
}

/** Fußballschal unter dem Kopf. */
private fun DrawScope.drawScarf() {
    drawRoundRect(
        color = ScarfColor,
        topLeft = Offset(64f, 110f),
        size = Size(72f, 16f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRect(color = ScarfStripe, topLeft = Offset(80f, 110f), size = Size(7f, 16f))
    drawRect(color = ScarfStripe, topLeft = Offset(108f, 110f), size = Size(7f, 16f))
    drawRoundRect(
        color = ScarfColor,
        topLeft = Offset(118f, 120f),
        size = Size(15f, 28f),
        cornerRadius = CornerRadius(5f, 5f)
    )
    drawRect(color = ScarfStripe, topLeft = Offset(118f, 132f), size = Size(15f, 6f))
    drawRoundRect(
        color = Line,
        topLeft = Offset(64f, 110f),
        size = Size(72f, 16f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(2f)
    )
}

/** Funkelnde Sterne beim Jubeln. */
private fun DrawScope.drawSparkles(phase: Float) {
    val alpha = phase.coerceIn(0f, 1f)
    val scaleValue = 0.4f + 0.6f * phase

    fun star(path: Path, pivot: Offset, delay: Float) {
        val local = ((phase + delay) % 1f).coerceIn(0f, 1f)
        scale(scaleX = 0.4f + 0.6f * local, scaleY = 0.4f + 0.6f * local, pivot = pivot) {
            rotate(degrees = 25f * local, pivot = pivot) {
                drawPath(path, color = SparkleGold.copy(alpha = local))
                drawPath(
                    path,
                    color = SparkleGoldLine.copy(alpha = local),
                    style = Stroke(1.5f, join = StrokeJoin.Round)
                )
            }
        }
    }

    star(SparkleBigPath, Offset(34f, 73f), 0f)
    star(SparkleSmall2Path, Offset(173f, 60f), 0.3f)
    star(SparkleSmall3Path, Offset(179f, 137f), 0.6f)
    star(SparkleSmall4Path, Offset(25f, 143f), 0.9f)

    scale(scaleX = scaleValue, scaleY = scaleValue, pivot = Offset(52f, 28f)) {
        drawCircle(SparkleBlue.copy(alpha = alpha), radius = 4f, center = Offset(52f, 28f))
        drawCircle(
            SparkleBlueLine.copy(alpha = alpha),
            radius = 4f,
            center = Offset(52f, 28f),
            style = Stroke(1.5f)
        )
    }
    scale(scaleX = scaleValue, scaleY = scaleValue, pivot = Offset(150f, 22f)) {
        drawCircle(SparklePink.copy(alpha = alpha), radius = 4f, center = Offset(150f, 22f))
        drawCircle(
            SparklePinkLine.copy(alpha = alpha),
            radius = 4f,
            center = Offset(150f, 22f),
            style = Stroke(1.5f)
        )
    }
}
