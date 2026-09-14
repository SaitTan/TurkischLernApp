package de.turkischlernen.app.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.model.LearnItem
import de.turkischlernen.app.data.model.Word
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.theme.AppColors

/**
 * "Wiederholen": Wörterliste zum Nachhören plus zwei Übungs-Modi
 * (schwierige Wörter / alle gelernten Wörter).
 */
@Composable
fun PracticeScreen(
    progress: UserProgress,
    onSpeak: (String, Boolean) -> Unit,
    onPractice: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val learned: List<LearnItem> = progress.learnedItems.mapNotNull { Curriculum.item(it) }
    val mistakes: List<LearnItem> = progress.mistakeItems.mapNotNull { Curriculum.item(it) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "header") {
            Column {
                Text("Wiederholen", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Du hast ${learned.size} von ${Curriculum.totalItemCount} Wörtern gelernt.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
                ChunkyButton(
                    text = if (mistakes.isEmpty()) "KEINE SCHWIERIGEN WÖRTER 👍"
                    else "SCHWIERIGE WÖRTER ÜBEN (${mistakes.size})",
                    modifier = Modifier.fillMaxWidth(),
                    color = AppColors.Orange,
                    enabled = mistakes.isNotEmpty()
                ) { onPractice(mistakes.map { it.id }) }
                Spacer(Modifier.height(10.dp))
                ChunkyButton(
                    text = "ALLE WÖRTER ÜBEN",
                    modifier = Modifier.fillMaxWidth(),
                    color = AppColors.Blue,
                    enabled = learned.isNotEmpty()
                ) { onPractice(learned.shuffled().map { it.id }) }
                Spacer(Modifier.height(18.dp))
                Text("Meine Wörter", style = MaterialTheme.typography.titleLarge)
            }
        }

        if (learned.isEmpty()) {
            item(key = "empty") {
                Text(
                    "Noch keine Wörter gelernt – starte auf dem Lernpfad! 🚀",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(learned.sortedBy { it.de }, key = { it.id }) { item ->
            WordRow(
                item = item,
                isTricky = item.id in progress.mistakeItems,
                onSpeak = onSpeak
            )
        }
    }
}

@Composable
private fun WordRow(
    item: LearnItem,
    isTricky: Boolean,
    onSpeak: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { onSpeak(item.tr, false) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(item.emoji, fontSize = 30.sp)
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.tr, style = MaterialTheme.typography.titleMedium, color = AppColors.Blue)
                if ((item as? Word)?.known == true) Text(" ⭐", fontSize = 14.sp)
                if (isTricky) Text(" 🔁", fontSize = 14.sp)
            }
            Text(
                item.de,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text("🔊", fontSize = 22.sp)
    }
}
