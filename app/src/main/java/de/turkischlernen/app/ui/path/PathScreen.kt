package de.turkischlernen.app.ui.path

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.content.Curriculum
import de.turkischlernen.app.data.model.LearnUnit
import de.turkischlernen.app.data.model.Lesson
import de.turkischlernen.app.data.model.LessonKind
import de.turkischlernen.app.data.progress.ChestLogic
import de.turkischlernen.app.data.progress.LessonRounds
import de.turkischlernen.app.data.progress.NodeStage
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.components.ThickProgressBar
import de.turkischlernen.app.ui.components.darker
import de.turkischlernen.app.ui.mascot.Kangal
import de.turkischlernen.app.ui.mascot.MascotMood
import de.turkischlernen.app.ui.rewards.ChestOverlay
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Der Lernpfad: Einheiten untereinander, Lektionen als große runde Knoten im
 * Zickzack. Jede Lektion hat mehrere Runden; der Ring um den Knoten zeigt den Stand.
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
    val runden = progress.lessonRounds

    // Die erste Lektion, die noch nicht alle Runden hat – bis dorthin ist alles frei.
    val currentIndex = allLessons
        .indexOfFirst { !LessonRounds.isDone(runden[it.id] ?: 0) }
        .let { if (it < 0) allLessons.size else it }

    val container = LocalAppContainer.current
    val settings by container.settingsRepository.current.collectAsState()
    val scope = rememberCoroutineScope()

    var startLesson by remember { mutableStateOf<Lesson?>(null) }
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
                item(key = "tts") { TtsHintCard(onClick = onTtsWarningClick) }
            }

            Curriculum.units.forEach { unit ->
                item(key = "u_${unit.id}") {
                    UnitHeader(unit = unit, progress = progress)
                }
                items(items = unit.lessons, key = { it.id }) { lesson ->
                    val lessonIndex = allLessons.indexOfFirst { it.id == lesson.id }
                    val gespielt = runden[lesson.id] ?: 0
                    val stage = LessonRounds.stageOf(
                        rounds = gespielt,
                        unlocked = lessonIndex <= currentIndex
                    )

                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LessonNode(
                            lesson = lesson,
                            unit = unit,
                            stage = stage,
                            rounds = gespielt,
                            highlight = lessonIndex == currentIndex,
                            positionInUnit = lesson.index - 1,
                            onClick = {
                                if (stage == NodeStage.LOCKED) onLockedClick() else startLesson = lesson
                            }
                        )

                        // Nach jeder dritten Lektion wartet eine Truhe.
                        if (ChestLogic.chestAfter(lesson.index)) {
                            val truhenId = ChestLogic.chestId(lesson.id)
                            ChestNode(
                                opened = truhenId in progress.openedChests,
                                enabled = LessonRounds.isDone(gespielt)
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

        val gewaehlteLektion = startLesson
        if (gewaehlteLektion != null) {
            LessonStartCard(
                lesson = gewaehlteLektion,
                unit = Curriculum.units.first { einheit ->
                    einheit.lessons.any { it.id == gewaehlteLektion.id }
                },
                rounds = runden[gewaehlteLektion.id] ?: 0,
                onStart = {
                    startLesson = null
                    onLessonClick(gewaehlteLektion)
                },
                onDismiss = { startLesson = null }
            )
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

/** Karte beim Antippen eines Knotens: Titel, Rundenstand, Startknopf. */
@Composable
private fun LessonStartCard(
    lesson: Lesson,
    unit: LearnUnit,
    rounds: Int,
    onStart: () -> Unit,
    onDismiss: () -> Unit
) {
    val farbe = if (LessonRounds.isGold(rounds)) AppColors.Gold else Color(unit.colorHex)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(28.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(farbe)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (lesson.kind == LessonKind.TEST) "Prüfung: ${unit.title}" else lesson.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = LessonRounds.nextRoundLabel(rounds),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(14.dp))
            ChunkyButton(
                text = LessonRounds.buttonLabel(rounds),
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                textColor = farbe.darker(0.75f)
            ) { onStart() }
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
            .background(if (!opened && enabled) AppColors.Gold else AppColors.Locked)
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
    val fertig = unit.lessons.count { LessonRounds.isDone(progress.lessonRounds[it.id] ?: 0) }

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
            fraction = fertig.toFloat() / unit.lessons.size,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "$fertig von ${unit.lessons.size} Lektionen",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}

/**
 * Ein runder Lektions-Knoten im Zickzack-Pfad. Der Ring zeigt die geschafften
 * Runden, die Farbe den Zustand – ohne Häkchen oder Pokale.
 */
@Composable
private fun LessonNode(
    lesson: Lesson,
    unit: LearnUnit,
    stage: NodeStage,
    rounds: Int,
    highlight: Boolean,
    positionInUnit: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Zickzack-Versatz: 0, +48, +72, +48, 0, -48, -72, -48 dp …
    val pattern = listOf(0, 48, 72, 48, 0, -48, -72, -48)
    val offsetX = pattern[positionInUnit % pattern.size].dp

    val unitColor = Color(unit.colorHex)
    val nodeColor = when (stage) {
        NodeStage.LOCKED -> AppColors.Locked
        NodeStage.OPEN, NodeStage.IN_PROGRESS -> unitColor
        NodeStage.DONE -> AppColors.Green
        NodeStage.GOLD -> AppColors.Gold
    }

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (highlight && stage != NodeStage.LOCKED) 1.06f else 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseValue"
    )

    val ring by animateFloatAsState(
        targetValue = LessonRounds.ringFraction(rounds),
        animationSpec = tween(600),
        label = "ring"
    )

    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (highlight && stage != NodeStage.LOCKED) {
            Box(
                Modifier
                    .offset(x = offsetX)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, nodeColor, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (rounds > 0) "WEITER" else "LOS!",
                    style = MaterialTheme.typography.labelMedium,
                    color = nodeColor
                )
            }
            Spacer(Modifier.height(6.dp))
        }

        Box(
            Modifier
                .offset(x = offsetX)
                .size(92.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ring um den Knoten: zeigt die geschafften Runden.
            Canvas(Modifier.size(92.dp)) {
                val strich = 7f * density
                val rand = strich / 2f
                drawArc(
                    color = nodeColor.copy(alpha = 0.25f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(rand, rand),
                    size = Size(size.width - strich, size.height - strich),
                    style = Stroke(width = strich, cap = StrokeCap.Round)
                )
                if (ring > 0f) {
                    drawArc(
                        color = nodeColor,
                        startAngle = -90f,
                        sweepAngle = 360f * ring,
                        useCenter = false,
                        topLeft = Offset(rand, rand),
                        size = Size(size.width - strich, size.height - strich),
                        style = Stroke(width = strich, cap = StrokeCap.Round)
                    )
                }
            }

            // 3D-Kante
            Box(
                Modifier
                    .size(72.dp)
                    .offset(y = 6.dp)
                    .clip(CircleShape)
                    .background(nodeColor.darker())
                    .align(Alignment.Center)
            )
            Box(
                Modifier
                    .size(72.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(nodeColor)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (stage == NodeStage.LOCKED) "🔒" else "★",
                    fontSize = if (stage == NodeStage.LOCKED) 28.sp else 38.sp,
                    color = Color.White
                )
            }

            // Das Maskottchen sitzt neben der Lektion, die als Nächstes dran ist.
            if (highlight && stage != NodeStage.LOCKED) {
                val settings by LocalAppContainer.current.settingsRepository.current.collectAsState()
                Kangal(
                    mood = MascotMood.WAVE,
                    size = 70.dp,
                    avatar = settings.avatar,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 74.dp)
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
