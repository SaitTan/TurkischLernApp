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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saittan.turkischlernen.R

private data class TileColors(val bg: Color, val onBg: Color, val arrow: Color)

private val WordsColors = TileColors(
    bg = Color(0xFFFFD8D8),
    onBg = Color(0xFF7A1F1F),
    arrow = Color(0xFFE53935),
)
private val SituationsColors = TileColors(
    bg = Color(0xFFCBF3F0),
    onBg = Color(0xFF0F5C57),
    arrow = Color(0xFF26A69A),
)
private val GameColors = TileColors(
    bg = Color(0xFFFFE4B5),
    onBg = Color(0xFF6B4A00),
    arrow = Color(0xFFF57C00),
)

@Composable
fun HomeScreen(
    totalWords: Int,
    learnedCount: Int,
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
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "Türkçe",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = "Türkisch lernen",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))
        ProgressBadge(learned = learnedCount, total = totalWords)
        Spacer(Modifier.height(24.dp))

        BigTile(
            iconResId = R.drawable.openmoji_food_cat,
            colors = WordsColors,
            title = stringResource(R.string.words_tile),
            subtitle = stringResource(R.string.words_tile_subtitle),
            onClick = onWordsClick,
        )
        Spacer(Modifier.height(16.dp))
        BigTile(
            iconResId = R.drawable.openmoji_help,
            colors = SituationsColors,
            title = stringResource(R.string.situations_tile),
            subtitle = stringResource(R.string.situations_tile_subtitle),
            onClick = onSituationsClick,
        )
        Spacer(Modifier.height(16.dp))
        BigTile(
            iconResId = R.drawable.openmoji_game_cat,
            colors = GameColors,
            title = stringResource(R.string.game_tile),
            subtitle = stringResource(R.string.game_tile_subtitle),
            onClick = onGameClick,
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProgressBadge(learned: Int, total: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondary,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFFE082),
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "$learned / $total Wörter",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun BigTile(
    iconResId: Int,
    colors: TileColors,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .clickable(onClick = onClick),
        color = colors.bg,
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.White.copy(alpha = 0.55f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(76.dp),
                )
            }
            Spacer(Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.onBg,
                    fontWeight = FontWeight.Black,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onBg.copy(alpha = 0.8f),
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.arrow,
                modifier = Modifier.size(40.dp),
            )
        }
    }
}
