package de.turkischlernen.app.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.data.progress.StreakCalendar
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.theme.AppColors
import java.time.LocalDate

private val WEEKDAYS = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")

/**
 * Einblendung hinter der Flamme: die aktuelle Woche mit den Tagen, an denen
 * gelernt wurde, dazu aktuelle und längste Serie.
 */
@Composable
fun StreakOverlay(
    progress: UserProgress,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now()
) {
    val woche = remember(today) { StreakCalendar.weekOf(today) }
    val laengste = remember(progress.activeDays, progress.longestStreak) {
        maxOf(progress.longestStreak, StreakCalendar.longestStreak(progress.activeDays))
    }

    Box(
        modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔥", fontSize = 56.sp)
            Text(
                text = if (progress.streakDays == 1) "1 Tag Serie" else "${progress.streakDays} Tage Serie",
                style = MaterialTheme.typography.headlineSmall,
                color = AppColors.Orange
            )
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                woche.forEachIndexed { index, tag ->
                    val aktiv = StreakCalendar.isActive(tag, progress.activeDays)
                    val istHeute = tag == today

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            WEEKDAYS[index],
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (aktiv) AppColors.Orange
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (istHeute) 3.dp else 0.dp,
                                    color = if (istHeute) AppColors.Gold else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (aktiv) "🔥" else tag.dayOfMonth.toString(),
                                fontSize = if (aktiv) 18.sp else 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Längste Serie: $laengste " + if (laengste == 1) "Tag" else "Tage",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Lerne jeden Tag, damit die Flamme weiterbrennt!",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(22.dp))
            ChunkyButton(
                text = "SCHLIESSEN",
                modifier = Modifier.fillMaxWidth(),
                color = AppColors.Orange
            ) { onClose() }
        }
    }
}
