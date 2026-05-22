package com.saittan.turkischlernen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saittan.turkischlernen.R
import com.saittan.turkischlernen.audio.TtsStatus
import com.saittan.turkischlernen.ui.components.TtsStatusChip

@Composable
fun HomeScreen(
    totalWords: Int,
    learnedCount: Int,
    ttsStatus: TtsStatus,
    onTestVoice: () -> Unit,
    onWordsClick: () -> Unit,
    onSituationsClick: () -> Unit,
    onGameClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Türkisch Lernen",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.home_progress, learnedCount, totalWords),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(Modifier.height(12.dp))
        TtsStatusChip(status = ttsStatus, onTest = onTestVoice)
        Spacer(Modifier.height(16.dp))

        HomeTile(
            iconResId = R.drawable.openmoji_food_cat,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            title = stringResource(R.string.words_tile),
            subtitle = stringResource(R.string.words_tile_subtitle),
            onClick = onWordsClick,
        )
        Spacer(Modifier.height(16.dp))
        HomeTile(
            iconResId = R.drawable.openmoji_help,
            backgroundColor = Color(0xFFCBF3F0),
            title = stringResource(R.string.situations_tile),
            subtitle = stringResource(R.string.situations_tile_subtitle),
            onClick = onSituationsClick,
        )
        Spacer(Modifier.height(16.dp))
        HomeTile(
            iconResId = R.drawable.openmoji_game_cat,
            backgroundColor = Color(0xFFFFE4B5),
            title = stringResource(R.string.game_tile),
            subtitle = stringResource(R.string.game_tile_subtitle),
            onClick = onGameClick,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HomeTile(
    iconResId: Int,
    backgroundColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        tonalElevation = 0.dp,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
            )
            Spacer(Modifier.size(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
