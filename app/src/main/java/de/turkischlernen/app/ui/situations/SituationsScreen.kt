package de.turkischlernen.app.ui.situations

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.data.content.Situations
import de.turkischlernen.app.data.model.Situation
import de.turkischlernen.app.ui.theme.AppColors

/**
 * Bereich "Ich brauche …": große Bildkarten. Ein Tipp liest den türkischen
 * Satz vor, langes Drücken liest ihn langsam vor.
 */
@Composable
fun SituationsScreen(
    onSpeak: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(
                    "Ich brauche …",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "Tippe auf eine Karte – die App sagt den Satz auf Türkisch. " +
                        "Halte die Karte gedrückt für langsames Sprechen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        items(Situations.all, key = { it.id }) { situation ->
            SituationCard(situation = situation, onSpeak = onSpeak)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SituationCard(
    situation: Situation,
    onSpeak: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .combinedClickable(
                onClick = { onSpeak(situation.tr, false) },
                onLongClick = { onSpeak(situation.tr, true) }
            )
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(situation.emoji, fontSize = 54.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            situation.de,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            situation.tr,
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.Blue,
            textAlign = TextAlign.Center
        )
        Text(
            situation.hint,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text("🔊", fontSize = 20.sp)
    }
}
