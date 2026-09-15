package de.turkischlernen.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import kotlin.math.roundToInt

/** Wackelt kurz waagerecht, sobald [trigger] einen neuen Wert größer 0 bekommt. */
fun Modifier.shake(trigger: Int): Modifier = composed {
    val offset = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        offset.snapTo(0f)
        offset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 400
                -14f at 50
                14f at 120
                -10f at 190
                10f at 260
                -4f at 330
            }
        )
    }
    graphicsLayer { translationX = offset.value * density }
}

/** Hüpft kurz, sobald [trigger] einen neuen Wert größer 0 bekommt. */
fun Modifier.bounce(trigger: Int): Modifier = composed {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        scale.snapTo(1f)
        scale.animateTo(1.08f, tween(110))
        scale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )
    }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/** Gibt beim Drücken leicht nach und federt zurück. */
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.95f
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pressScale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Federt beim Sichtbarwerden ein. Der Platz im Layout bleibt immer reserviert,
 * damit nichts springt.
 */
fun Modifier.popIn(visible: Boolean): Modifier = composed {
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "popIn"
    )
    graphicsLayer {
        alpha = progress.coerceIn(0f, 1f)
        val scale = 0.5f + 0.5f * progress
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Zahl, die von 0 auf [target] hochzählt, sobald [start] true wird.
 * [onTick] wird dabei gleichmäßig verteilt [ticks]-mal aufgerufen (z. B. für Sounds).
 */
@Composable
fun AnimatedCounter(
    target: Int,
    start: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    suffix: String = "",
    durationMillis: Int = 900,
    ticks: Int = 0,
    onTick: () -> Unit = {}
) {
    val value = remember { Animatable(0f) }
    LaunchedEffect(target, start) {
        if (!start) return@LaunchedEffect
        var lastTick = 0
        value.animateTo(
            targetValue = target.toFloat(),
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        ) {
            if (ticks > 0 && target > 0) {
                val reached = (this.value / target * ticks).toInt()
                if (reached > lastTick) {
                    lastTick = reached
                    onTick()
                }
            }
        }
    }
    Text(
        text = "${value.value.roundToInt()}$suffix",
        style = style,
        color = color,
        modifier = modifier
    )
}
