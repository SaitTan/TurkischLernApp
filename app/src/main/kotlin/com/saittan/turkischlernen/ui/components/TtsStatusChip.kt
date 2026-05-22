package com.saittan.turkischlernen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.saittan.turkischlernen.audio.TtsStatus

@Composable
fun TtsStatusChip(status: TtsStatus, onTest: () -> Unit) {
    val (bg, fg, label, icon) = when (status) {
        TtsStatus.Initializing -> Quad(Color(0xFFE0E0E0), Color(0xFF555555), "Stimme wird geladen…", Icons.Filled.VolumeOff)
        TtsStatus.Ready -> Quad(Color(0xFFC8E6C9), Color(0xFF1B5E20), "Türkische Stimme bereit · TEST", Icons.Filled.VolumeUp)
        TtsStatus.TurkishMissing -> Quad(Color(0xFFFFE0B2), Color(0xFFB71C1C), "Türkische Stimme fehlt · TEST", Icons.Filled.VolumeOff)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onTest)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = fg, modifier = Modifier.size(20.dp))
        Text(
            text = "  $label",
            style = MaterialTheme.typography.bodyLarge,
            color = fg,
        )
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
