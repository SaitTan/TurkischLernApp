package de.turkischlernen.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.turkischlernen.app.data.model.LearnItem
import de.turkischlernen.app.data.model.Word

/**
 * Zeigt ein Vokabel-Bild. Farben werden als großer Farbkreis dargestellt,
 * alles andere als großes Emoji – so braucht die App keine Bilddateien und
 * funktioniert vollständig offline.
 */
@Composable
fun ItemIllustration(
    item: LearnItem,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp
) {
    val colorHex = (item as? Word)?.colorHex
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        if (colorHex != null) {
            Box(
                Modifier
                    .size(size * 0.8f)
                    .clip(CircleShape)
                    .background(Color(colorHex))
                    .border(3.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        } else {
            val fontSize = with(LocalDensity.current) { (size * 0.62f).toSp() }
            Text(text = item.emoji, fontSize = fontSize)
        }
    }
}

/** Kleine Variante für Listen und Antwortkacheln. */
@Composable
fun ItemIcon(item: LearnItem, size: Dp = 44.dp, modifier: Modifier = Modifier) {
    ItemIllustration(item = item, modifier = modifier, size = size)
}
