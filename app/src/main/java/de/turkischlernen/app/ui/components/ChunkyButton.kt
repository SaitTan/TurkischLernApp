package de.turkischlernen.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.turkischlernen.app.ui.theme.AppColors

/** Dunklere Variante einer Farbe – für die 3D-Kante der Buttons. */
fun Color.darker(factor: Float = 0.78f): Color =
    Color(red * factor, green * factor, blue * factor, alpha)

/**
 * Großer, runder Button mit "3D"-Kante, der beim Drücken nach unten federt –
 * das klassische Gefühl aus Lern-Apps für Kinder.
 */
@Composable
fun ChunkyButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AppColors.Green,
    textColor: Color = Color.White,
    enabled: Boolean = true,
    height: Dp = 56.dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val edge = 5.dp
    val activeColor = if (enabled) color else AppColors.Locked
    val activeTextColor = if (enabled) textColor else Color(0xFFAFAFAF)
    val sink = if (pressed && enabled) edge else 0.dp

    Box(modifier = modifier.height(height + edge)) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(16.dp))
                .background(activeColor.darker())
        )
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = sink)
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(16.dp))
                .background(activeColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = activeTextColor,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

/** Button mit Rahmen statt Füllung (z. B. "Überspringen"). */
@Composable
fun OutlineChunkyButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AppColors.Blue,
    height: Dp = 52.dp,
    onClick: () -> Unit
) {
    Box(
        modifier
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
