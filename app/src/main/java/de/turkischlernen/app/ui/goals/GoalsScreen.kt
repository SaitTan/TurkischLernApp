package de.turkischlernen.app.ui.goals

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.content.DailyQuests
import de.turkischlernen.app.data.progress.ChestLogic
import de.turkischlernen.app.data.progress.LessonRounds
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.ThickProgressBar
import de.turkischlernen.app.ui.path.DailyQuestsCard
import de.turkischlernen.app.ui.rewards.ChestOverlay
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

/**
 * Der Tab "Ziele": Tagesziel, die drei Tagesaufgaben, die Tages-Truhe und eine
 * Übersicht, wie viele Truhen auf dem Lernpfad noch warten.
 */
@Composable
fun GoalsScreen(
    progress: UserProgress,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val settings by container.settingsRepository.current.collectAsState()
    val scope = rememberCoroutineScope()

    val heute = remember { LocalDate.now() }
    val quests = remember(heute) { DailyQuests.forDay(heute) }
    val tagesTruhe = ChestLogic.dailyChestId(heute)
    val alleErledigt = DailyQuests.allDone(heute, progress)
    val tagesTruheOffen = tagesTruhe in progress.openedChests

    val truhenAufDemPfad = remember(progress.lessonRounds, progress.openedChests) {
        Curriculum.lessons.count { lektion ->
            ChestLogic.chestAfter(lektion.index) &&
                LessonRounds.isDone(progress.lessonRounds[lektion.id] ?: 0) &&
                ChestLogic.chestId(lektion.id) !in progress.openedChests
        }
    }

    var chestToOpen by remember { mutableStateOf<String?>(null) }
    val reward = remember(chestToOpen, progress.unlockedItems) {
        chestToOpen?.let { ChestLogic.roll(Random(it.hashCode()), progress.unlockedItems) }
    }

    Box(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item(key = "titel") {
                Text(
                    "Ziele",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
            }

            item(key = "tagesziel") { DailyGoalCard(progress) }

            item(key = "aufgaben") { DailyQuestsCard(quests = quests, progress = progress) }

            item(key = "tagestruhe") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tages-Truhe", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    when {
                        tagesTruheOffen -> Text(
                            "Heute schon geöffnet – morgen gibt es eine neue.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        alleErledigt -> {
                            Text(
                                "Alle Tagesaufgaben geschafft!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.GreenDark
                            )
                            Spacer(Modifier.height(8.dp))
                            ChestButton(enabled = true) { chestToOpen = tagesTruhe }
                        }

                        else -> {
                            Text(
                                "Schaffe alle drei Tagesaufgaben, dann öffnet sich die Truhe.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            ChestButton(enabled = false) { }
                        }
                    }
                }
            }

            item(key = "pfadtruhen") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                ) {
                    Text("Truhen auf dem Lernpfad", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = when (truhenAufDemPfad) {
                            0 -> "Gerade wartet keine Truhe. Nach jeder dritten Lektion kommt eine neue."
                            1 -> "1 Truhe wartet auf dich – schau im Tab „Lernen“ nach."
                            else -> "$truhenAufDemPfad Truhen warten auf dich – schau im Tab „Lernen“ nach."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        val offeneTruhe = chestToOpen
        if (offeneTruhe != null && reward != null) {
            ChestOverlay(
                reward = reward,
                avatar = settings.avatar,
                onClose = {
                    scope.launch { container.progressRepository.openChest(offeneTruhe, reward) }
                    chestToOpen = null
                }
            )
        }
    }
}

/** Tagesziel mit Fortschrittsbalken. */
@Composable
fun DailyGoalCard(progress: UserProgress, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (progress.dailyGoalReached) "Tagesziel geschafft! 🎉" else "Tagesziel",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${progress.xpToday} / ${progress.dailyGoal} XP",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(10.dp))
        ThickProgressBar(
            fraction = if (progress.dailyGoal == 0) 1f
            else progress.xpToday.toFloat() / progress.dailyGoal,
            modifier = Modifier.fillMaxWidth(),
            color = AppColors.Gold,
            trackColor = MaterialTheme.colorScheme.background
        )
    }
}

/** Runder Truhen-Knopf; gesperrt wird er grau und pulsiert nicht. */
@Composable
private fun ChestButton(enabled: Boolean, onClick: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "truhe").animateFloat(
        initialValue = 1f,
        targetValue = if (enabled) 1.08f else 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "truhePuls"
    )

    Box(
        Modifier
            .size(76.dp)
            .scale(pulse)
            .clip(CircleShape)
            .background(if (enabled) AppColors.Gold else AppColors.Locked)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(if (enabled) "🎁" else "🔒", fontSize = 34.sp)
    }
}
