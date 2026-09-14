package de.turkischlernen.app.ui.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.components.ConfettiOverlay
import de.turkischlernen.app.ui.theme.AppColors

/** Abschlussbildschirm mit Konfetti, XP und Serie. */
@Composable
fun LessonCompleteScreen(
    earnedXp: Int,
    mistakes: Int,
    streakDays: Int,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(if (mistakes == 0) "🏆" else "🎉", fontSize = 96.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (mistakes == 0) "Perfekt!" else "Geschafft!",
                style = MaterialTheme.typography.displaySmall,
                color = AppColors.GreenDark,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (mistakes == 0) {
                    "Alles richtig – du bist ein Türkisch-Profi! 🌟"
                } else {
                    "Weiter so! Übung macht den Meister."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultCard("⭐", "$earnedXp XP", "gesammelt", AppColors.Gold, Modifier.weight(1f))
                ResultCard(
                    "🔥", "$streakDays", if (streakDays == 1) "Tag Serie" else "Tage Serie",
                    AppColors.Orange, Modifier.weight(1f)
                )
                ResultCard(
                    "🎯", "$mistakes", if (mistakes == 1) "Fehler" else "Fehler",
                    AppColors.Blue, Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(36.dp))
            ChunkyButton("WEITER", Modifier.fillMaxWidth()) { onContinue() }
        }

        ConfettiOverlay()
    }
}

@Composable
private fun ResultCard(
    emoji: String,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
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
        Text(value, style = MaterialTheme.typography.titleLarge, color = color)
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
