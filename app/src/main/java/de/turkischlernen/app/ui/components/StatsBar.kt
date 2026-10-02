package de.turkischlernen.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.theme.AppColors

/** Kopfzeile mit Serie (Streak), XP und Herzen. */
@Composable
fun StatsBar(
    progress: UserProgress,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatChip("🔥", progress.streakDays.toString(), AppColors.Orange)
        StatChip("⭐", progress.totalXp.toString(), AppColors.Gold)
        StatChip(
            emoji = "❤️",
            value = if (progress.unlimitedHearts) "∞" else progress.hearts.toString(),
            color = AppColors.Red
        )
    }
}

@Composable
fun StatChip(
    emoji: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(
            text = value,
            color = color,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/** Dicker, runder Fortschrittsbalken (Lektion / Tagesziel). [glow] färbt ihn kurz golden. */
@Composable
fun ThickProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    color: Color = AppColors.Green,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    glow: Boolean = false
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "progress"
    )
    val barColor by animateColorAsState(
        targetValue = if (glow) AppColors.Gold else color,
        animationSpec = tween(250),
        label = "progressGlow"
    )
    Box(
        modifier
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(trackColor)
    ) {
        Box(
            Modifier
                // Die Feder schwingt leicht über – fillMaxWidth verlangt 0..1.
                .fillMaxWidth(animatedFraction.coerceIn(0f, 1f))
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(barColor)
        )
    }
}
