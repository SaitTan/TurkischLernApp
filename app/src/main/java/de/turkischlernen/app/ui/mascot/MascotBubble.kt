package de.turkischlernen.app.ui.mascot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import de.turkischlernen.app.data.content.MascotPhrase
import de.turkischlernen.app.ui.components.popIn
import de.turkischlernen.app.ui.theme.AppColors

/**
 * Sprechblase des Maskottchens: türkischer Ruf groß, deutsche Übersetzung klein.
 * Ist [phrase] null, bleibt der Platz reserviert und die Blase unsichtbar.
 */
@Composable
fun MascotBubble(phrase: MascotPhrase?, modifier: Modifier = Modifier) {
    Box(modifier.popIn(phrase != null), contentAlignment = Alignment.CenterStart) {
        Box(contentAlignment = Alignment.CenterStart) {
            // Zipfel zur Figur hin
            Canvas(Modifier.size(10.dp)) {
                val tail = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(0f, size.height / 2f)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(tail, color = AppColors.GreenLight)
            }
            Column(
                Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.GreenLight)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = phrase?.tr.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.GreenDark
                )
                Text(
                    text = phrase?.de.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.GreenDark.copy(alpha = 0.75f)
                )
            }
        }
    }
}
