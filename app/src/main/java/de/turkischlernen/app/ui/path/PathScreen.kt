package de.turkischlernen.app.ui.path

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.content.DailyQuests
import de.turkischlernen.app.data.progress.ChestLogic
import de.turkischlernen.app.data.model.LearnUnit
import de.turkischlernen.app.data.model.Lesson
import de.turkischlernen.app.data.model.LessonKind
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.ThickProgressBar
import de.turkischlernen.app.ui.mascot.Kangal
import de.turkischlernen.app.ui.rewards.ChestOverlay
import de.turkischlernen.app.ui.mascot.MascotMood
import de.turkischlernen.app.ui.components.darker
import de.turkischlernen.app.ui.theme.AppColors

/** Zustand eines Knotens auf dem Lernpfad. */
enum class NodeState { DONE, CURRENT, LOCKED }

/**
 * Der Lernpfad: Einheiten untereinander, Lektionen als große runde Knoten im
 * Zickzack – genau wie in den bekannten Sprachlern-Apps.
 */
@Composable
fun PathScreen(
    progress: UserProgress,
    ttsWarning: Boolean,
    onLessonClick: (Lesson) -> Unit,
    onLockedClick: () -> Unit,
    onTtsWarningClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allLessons = Curriculum.lessons
    val currentIndex = allLessons.indexOfFirst { it.id !in progress.completedLessons }
        .let { if (it < 0) allLessons.size else it }

    val container = LocalAppContainer.current
    val settings by container.settingsRepository.current.collectAsState()
    val scope = rememberCoroutineScope()
    val heute = remember { LocalDate.now() }
    val quests = remember(heute) { DailyQuests.forDay(heute) }

    // Welche Truhe gerade offen ist; der Inhalt steht pro Truhe fest.
    var chestToOpen by remember { mutableStateOf<String?>(null) }
    val reward = remember(chestToOpen, progress.unlockedItems) {
        chestToOpen?.let { ChestLogic.roll(Random(it.hashCode()), progress.unlockedItems) }
    }

    Box(modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (ttsWarning) {
            item(key = "tts") {
                TtsHintCard(onClick = onTtsWarningClick)
            }
        }
        item(key = "goal") {
            DailyGoalCard(progress)
        }

        item(key = "quests") {
            DailyQuestsCard(quests = quests, progress = progress)
        }

        val tagesTruhe = ChestLogic.dailyChestId(heute)
        if (DailyQuests.allDone(heute, progress) && tagesTruhe !in progress.openedChests) {
            item(key = "tagestruhe") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Alle Tagesaufgaben geschafft!",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.GreenDark
                    )
                    ChestNode(opened = false, enabled = true) { chestToOpen = tagesTruhe }
                }
            }
        }

        Curriculum.units.forEach { unit ->
            item(key = "u_${unit.id}") {
                UnitHeader(unit = unit, progress = progress)
            }
            items(items = unit.lessons, key = { it.id }) { lesson ->
                val lessonIndex = allLessons.indexOfFirst { it.id == lesson.id }
                val state = when {
                    lesson.id in progress.completedLessons -> NodeState.DONE
                    lessonIndex <= currentIndex -> NodeState.CURRENT
                    else -> NodeState.LOCKED
                }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LessonNode(
                        lesson = lesson,
                        unit = unit,
                        state = state,
                        positionInUnit = lesson.index - 1,
                        onClick = {
                            if (state == NodeState.LOCKED) onLockedClick() else onLessonClick(lesson)
                        }
                    )

                    // Nach jeder dritten Lektion wartet eine Truhe.
                    if (ChestLogic.chestAfter(lesson.index)) {
                        val truhenId = ChestLogic.chestId(lesson.id)
                        ChestNode(
                            opened = truhenId in progress.openedChests,
                            enabled = lesson.id in progress.completedLessons
                        ) { chestToOpen = truhenId }
                    }
                }
            }
        }

        item(key = "end") {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🏁", fontSize = 44.sp)
                Text(
                    "Das ist das Ende – super gemacht!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
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

/** Truhen-Knoten auf dem Lernpfad. */
@Composable
private fun ChestNode(
    opened: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val pulse by rememberInfiniteTransition(label = "truhe").animateFloat(
        initialValue = 1f,
        targetValue = if (enabled && !opened) 1.08f else 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "truhePuls"
    )

    Box(
        modifier
            .padding(vertical = 10.dp)
            .size(74.dp)
            .scale(pulse)
            .clip(CircleShape)
            .background(
                when {
                    opened -> AppColors.Locked
                    enabled -> AppColors.Gold
                    else -> AppColors.Locked
                }
            )
            .clickable(enabled = enabled && !opened) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when {
                opened -> "📭"
                enabled -> "🎁"
                else -> "🔒"
            },
            fontSize = 32.sp
        )
    }
}

@Composable
private fun DailyGoalCard(progress: UserProgress, modifier: Modifier = Modifier) {
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
            color = AppColors.Gold
        )
    }
}

@Composable
private fun TtsHintCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.RedLight)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("🔇", fontSize = 26.sp)
        Text(
            "Türkische Sprachausgabe fehlt noch. Hier tippen, um sie zu installieren.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8A2020)
        )
    }
}

@Composable
private fun UnitHeader(unit: LearnUnit, progress: UserProgress, modifier: Modifier = Modifier) {
    val done = unit.lessons.count { it.id in progress.completedLessons }
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(unit.colorHex))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(unit.emoji, fontSize = 34.sp)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.fillMaxWidth()) {
                Text(
                    unit.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    unit.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        ThickProgressBar(
            fraction = done.toFloat() / unit.lessons.size,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "$done von ${unit.lessons.size} Lektionen",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}

/** Ein runder Lektions-Knoten im Zickzack-Pfad. */
@Composable
private fun LessonNode(
    lesson: Lesson,
    unit: LearnUnit,
    state: NodeState,
    positionInUnit: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Zickzack-Versatz: 0, +48, +72, +48, 0, -48, -72, -48 dp …
    val pattern = listOf(0, 48, 72, 48, 0, -48, -72, -48)
    val offsetX = pattern[positionInUnit % pattern.size].dp

    val unitColor = Color(unit.colorHex)
    val nodeColor = when (state) {
        NodeState.DONE -> AppColors.Gold
        NodeState.CURRENT -> unitColor
        NodeState.LOCKED -> AppColors.Locked
    }

    val settings by LocalAppContainer.current.settingsRepository.current.collectAsState()

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (state == NodeState.CURRENT) 1.06f else 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseValue"
    )

    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state == NodeState.CURRENT) {
            Box(
                Modifier
                    .offset(x = offsetX)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, unitColor, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    "LOS!",
                    style = MaterialTheme.typography.labelMedium,
                    color = unitColor
                )
            }
            Spacer(Modifier.height(6.dp))
        }

        Box(
            Modifier
                .offset(x = offsetX)
                .size(84.dp),
            contentAlignment = Alignment.Center
        ) {
            // 3D-Kante
            Box(
                Modifier
                    .size(76.dp)
                    .offset(y = 6.dp)
                    .clip(CircleShape)
                    .background(nodeColor.darker())
                    .align(Alignment.Center)
            )
            Box(
                Modifier
                    .size(76.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(nodeColor)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        state == NodeState.LOCKED -> "🔒"
                        state == NodeState.DONE && lesson.kind == LessonKind.TEST -> "🏆"
                        state == NodeState.DONE -> "✅"
                        lesson.kind == LessonKind.TEST -> "👑"
                        else -> "⭐"
                    },
                    fontSize = 34.sp
                )
            }

            // Das Maskottchen sitzt neben der Lektion, die als Nächstes dran ist.
            if (state == NodeState.CURRENT) {
                Kangal(
                    mood = MascotMood.WAVE,
                    size = 70.dp,
                    avatar = settings.avatar,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 62.dp)
                )
            }
        }

        Text(
            text = lesson.title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .offset(x = offsetX)
                .padding(top = 6.dp)
        )
    }
}
