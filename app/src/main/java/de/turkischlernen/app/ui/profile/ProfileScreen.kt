package de.turkischlernen.app.ui.profile

import android.content.Intent
import android.speech.tts.TextToSpeech
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.progress.Achievements
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.launch

/** Profil mit Statistik, Abzeichen und Eltern-Bereich. */
@Composable
fun ProfileScreen(
    progress: UserProgress,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showReset by remember { mutableStateOf(false) }
    var showParentArea by remember { mutableStateOf(false) }
    val settings by container.settingsRepository.current.collectAsState()

    val achievements = Achievements.forProgress(progress)
    val doneLessons = progress.completedLessons.size

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "head") {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("🦉", fontSize = 66.sp)
                Text("Mein Türkisch", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Serie: ${progress.streakDays} Tage",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item(key = "stats") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox("⭐", "${progress.totalXp}", "XP", AppColors.Gold, Modifier.weight(1f))
                StatBox("🔥", "${progress.streakDays}", "Serie", AppColors.Orange, Modifier.weight(1f))
                StatBox(
                    "🔤", "${progress.learnedItems.size}", "Wörter",
                    AppColors.Blue, Modifier.weight(1f)
                )
                StatBox("✅", "$doneLessons", "Lektionen", AppColors.Green, Modifier.weight(1f))
            }
        }

        item(key = "overall") {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Text("Gesamtfortschritt", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    "$doneLessons von ${Curriculum.lessons.size} Lektionen · " +
                        "${progress.learnedItems.size} von ${Curriculum.totalItemCount} Wörtern",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item(key = "ach_title") {
            Text("Abzeichen", style = MaterialTheme.typography.titleLarge)
        }

        for (chunk in achievements.chunked(3)) {
            item(key = "ach_${chunk.first().id}") {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    chunk.forEach { achievement ->
                        Column(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    2.dp,
                                    if (achievement.unlocked) AppColors.Gold
                                    else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                if (achievement.unlocked) achievement.emoji else "🔒",
                                fontSize = 30.sp
                            )
                            Text(
                                achievement.title,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                achievement.description,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    // Reihe auffüllen, damit die Kacheln gleich groß bleiben.
                    repeat(3 - chunk.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        item(key = "parents") {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { showParentArea = !showParentArea }
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👨‍👩‍👧 Eltern-Bereich", style = MaterialTheme.typography.titleMedium)
                    Text(if (showParentArea) "▲" else "▼")
                }
            }
        }

        if (showParentArea) {
            item(key = "settings") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingSwitch(
                        title = "Unbegrenzte Herzen",
                        subtitle = "Aus: Bei Fehlern gehen Herzen verloren (wie in Duolingo).",
                        checked = progress.unlimitedHearts,
                        onCheckedChange = { enabled ->
                            scope.launch { container.progressRepository.setUnlimitedHearts(enabled) }
                        }
                    )

                    SettingSwitch(
                        title = "Sounds",
                        subtitle = "Töne bei Antworten und Belohnungen. Die Aussprache bleibt an.",
                        checked = settings.soundEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch { container.settingsRepository.setSoundEnabled(enabled) }
                        }
                    )

                    SettingSwitch(
                        title = "Vibration",
                        subtitle = "Kurze Vibration bei richtigen und falschen Antworten.",
                        checked = settings.hapticsEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch { container.settingsRepository.setHapticsEnabled(enabled) }
                        }
                    )

                    Column {
                        Text("Tagesziel", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(15, 30, 50).forEach { goal ->
                                val selected = progress.dailyGoal == goal
                                GoalChip(
                                    selected = selected,
                                    label = "$goal XP",
                                    onClick = {
                                        scope.launch {
                                            container.progressRepository.setDailyGoal(goal)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        "Sprachausgabe",
                        style = MaterialTheme.typography.titleMedium
                    )
                    SettingButton("🔊 Türkische Aussprache testen") {
                        container.speech.speak("Merhaba! Türkçe öğreniyoruz.", false)
                    }
                    SettingButton("⬇️ Türkische Sprachdaten installieren") {
                        runCatching {
                            context.startActivity(
                                Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                    SettingButton("🗑️ Fortschritt zurücksetzen", AppColors.Red) {
                        showReset = true
                    }

                    Text(
                        "Die App speichert alles nur auf diesem Gerät. " +
                            "Kein Login, keine Werbung, kein Internet nötig.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text("Fortschritt zurücksetzen?") },
            text = { Text("Alle Sterne, XP und Serien werden gelöscht. Das kann nicht rückgängig gemacht werden.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { container.progressRepository.resetProgress() }
                    showReset = false
                }) { Text("Zurücksetzen") }
            },
            dismissButton = {
                TextButton(onClick = { showReset = false }) { Text("Abbrechen") }
            }
        )
    }
}

@Composable
private fun StatBox(
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
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingButton(
    text: String,
    color: Color = AppColors.Blue,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp)
    )
}

/** Auswahl-Chip für das Tagesziel. */
@Composable
private fun GoalChip(selected: Boolean, label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) AppColors.Green else Color.Transparent)
            .border(
                2.dp,
                if (selected) AppColors.Green else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/** Einstellungszeile mit Titel, Erklärung und Schalter. */
@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
