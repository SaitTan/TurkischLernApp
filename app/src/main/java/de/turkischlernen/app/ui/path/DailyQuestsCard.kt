package de.turkischlernen.app.ui.path

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.data.content.DailyQuests
import de.turkischlernen.app.data.content.Quest
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.ThickProgressBar
import de.turkischlernen.app.ui.theme.AppColors

/**
 * Die drei Tagesaufgaben mit Fortschrittsbalken. Erledigte Aufgaben bekommen
 * einen grünen Haken.
 */
@Composable
fun DailyQuestsCard(
    quests: List<Quest>,
    progress: UserProgress,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tagesaufgaben", style = MaterialTheme.typography.titleMedium)
            val erledigt = quests.count { DailyQuests.isDone(it, progress) }
            Text(
                "$erledigt / ${quests.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        quests.forEach { quest ->
            val stand = DailyQuests.progressOf(quest, progress).coerceAtMost(quest.goal)
            val fertig = stand >= quest.goal

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(quest.emoji, fontSize = 22.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        quest.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (fertig) AppColors.GreenDark
                        else MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(4.dp))
                    ThickProgressBar(
                        fraction = stand.toFloat() / quest.goal,
                        modifier = Modifier.fillMaxWidth(),
                        color = if (fertig) AppColors.Green else AppColors.Gold,
                        // Die Karte hat selbst die Farbe surfaceVariant – ohne eigenen
                        // Hintergrund waere eine leere Leiste unsichtbar.
                        trackColor = MaterialTheme.colorScheme.background
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (fertig) "✅" else "$stand/${quest.goal}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
