package de.turkischlernen.app.ui.lesson

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import de.turkischlernen.app.data.model.Exercise
import de.turkischlernen.app.data.model.LearnItem
import de.turkischlernen.app.ui.components.ItemIllustration
import de.turkischlernen.app.ui.components.bounce
import de.turkischlernen.app.ui.components.pressScale
import de.turkischlernen.app.ui.components.shake
import de.turkischlernen.app.ui.theme.AppColors

/** Farbzustand einer Antwortkachel. */
enum class OptionStatus { NORMAL, SELECTED, CORRECT, WRONG }

@Composable
fun OptionCard(
    text: String,
    status: OptionStatus,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val sounds = LocalAppContainer.current.sounds
    val interactionSource = remember { MutableInteractionSource() }

    // Wechsel auf richtig/falsch löst Hüpfen bzw. Wackeln aus.
    var shakeTrigger by remember { mutableIntStateOf(0) }
    var bounceTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(status) {
        when (status) {
            OptionStatus.WRONG -> shakeTrigger++
            OptionStatus.CORRECT -> bounceTrigger++
            else -> Unit
        }
    }

    val border = when (status) {
        OptionStatus.NORMAL -> MaterialTheme.colorScheme.outline
        OptionStatus.SELECTED -> AppColors.Blue
        OptionStatus.CORRECT -> AppColors.Green
        OptionStatus.WRONG -> AppColors.Red
    }
    val fill = when (status) {
        OptionStatus.NORMAL -> Color.Transparent
        OptionStatus.SELECTED -> AppColors.BlueLight
        OptionStatus.CORRECT -> AppColors.GreenLight
        OptionStatus.WRONG -> AppColors.RedLight
    }
    val textColor = when (status) {
        OptionStatus.NORMAL -> MaterialTheme.colorScheme.onBackground
        OptionStatus.SELECTED -> AppColors.BlueDark
        OptionStatus.CORRECT -> AppColors.GreenDark
        OptionStatus.WRONG -> AppColors.RedDark
    }

    Box(
        modifier
            .fillMaxWidth()
            .shake(shakeTrigger)
            .bounce(bounceTrigger)
            .pressScale(interactionSource)
            .heightIn(min = 62.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(fill)
            .border(2.dp, border, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled
            ) {
                sounds.tap()
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = textColor)
    }
}

/** Runder Lautsprecher-Knopf. */
@Composable
fun SpeakerButton(
    modifier: Modifier = Modifier,
    big: Boolean = false,
    color: Color = AppColors.Blue,
    onClick: () -> Unit
) {
    val size = if (big) 110.dp else 56.dp
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("🔊", fontSize = if (big) 48.sp else 26.sp)
    }
}

/** Bild anschauen, türkisches Wort wählen. */
@Composable
fun PictureChoiceView(
    exercise: Exercise.PictureChoice,
    interaction: ExerciseInteraction,
    locked: Boolean,
    onSpeak: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var bounce by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (bounce) 1.12f else 1f, label = "bounce")
    LaunchedEffect(bounce) {
        if (bounce) {
            kotlinx.coroutines.delay(180)
            bounce = false
        }
    }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .scale(scale)
                .clip(RoundedCornerShape(24.dp))
                .clickable {
                    bounce = true
                    onSpeak(exercise.target.tr, false)
                }
                .padding(8.dp)
        ) {
            ItemIllustration(exercise.target, size = 160.dp)
        }
        Text(
            "Tippe auf das Bild, um es zu hören 🔊",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
        OptionList(
            options = exercise.options,
            label = { it.tr },
            targetId = exercise.target.id,
            interaction = interaction,
            locked = locked,
            onSelect = { onSpeak(it.tr, false) }
        )
    }
}

/** Türkisches Wort lesen/hören, deutsche Bedeutung wählen. */
@Composable
fun TranslateView(
    exercise: Exercise.TranslateToGerman,
    interaction: ExerciseInteraction,
    locked: Boolean,
    onSpeak: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(exercise) { onSpeak(exercise.target.tr, false) }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SpeakerButton { onSpeak(exercise.target.tr, false) }
            Spacer(Modifier.size(14.dp))
            Text(
                exercise.target.tr,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            exercise.target.hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(22.dp))
        OptionList(
            options = exercise.options,
            label = { it.de },
            targetId = exercise.target.id,
            interaction = interaction,
            locked = locked,
            onSelect = { }
        )
    }
}

/** Nur hören: das richtige türkische Wort antippen. */
@Composable
fun ListeningView(
    exercise: Exercise.Listening,
    interaction: ExerciseInteraction,
    locked: Boolean,
    onSpeak: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(exercise) { onSpeak(exercise.target.tr, false) }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        SpeakerButton(big = true) { onSpeak(exercise.target.tr, false) }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                .clickable { onSpeak(exercise.target.tr, true) }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text("🐢 langsam", style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(22.dp))
        OptionList(
            options = exercise.options,
            label = { it.tr },
            targetId = exercise.target.id,
            interaction = interaction,
            locked = locked,
            onSelect = { onSpeak(it.tr, false) }
        )
    }
}

@Composable
private fun OptionList(
    options: List<LearnItem>,
    label: (LearnItem) -> String,
    targetId: String,
    interaction: ExerciseInteraction,
    locked: Boolean,
    onSelect: (LearnItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { option ->
            val selected = interaction.selectedItemId == option.id
            val status = when {
                locked && option.id == targetId -> OptionStatus.CORRECT
                locked && selected -> OptionStatus.WRONG
                selected -> OptionStatus.SELECTED
                else -> OptionStatus.NORMAL
            }
            OptionCard(text = label(option), status = status, enabled = !locked) {
                interaction.selectedItemId = option.id
                onSelect(option)
            }
        }
    }
}

/** Satz aus Wortkacheln bauen. [wrong] = Antwort wurde als falsch gewertet. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordBankView(
    exercise: Exercise.WordBank,
    interaction: ExerciseInteraction,
    locked: Boolean,
    onSpeak: (String, Boolean) -> Unit,
    wrong: Boolean = false,
    modifier: Modifier = Modifier
) {
    var rowShake by remember(exercise) { mutableIntStateOf(0) }
    var rowBounce by remember(exercise) { mutableIntStateOf(0) }
    LaunchedEffect(locked, wrong) {
        if (locked) {
            if (wrong) rowShake++ else rowBounce++
        }
    }

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(exercise.target.emoji, fontSize = 40.sp)
            Spacer(Modifier.size(12.dp))
            Text(
                "„${exercise.target.de}“",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.height(18.dp))

        // Antwortzeile
        Box(
            Modifier
                .fillMaxWidth()
                .shake(rowShake)
                .bounce(rowBounce)
                .heightIn(min = 76.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp)
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                interaction.chosenTiles.forEach { index ->
                    Tile(text = exercise.tiles[index], enabled = !locked) {
                        interaction.chosenTiles.remove(index)
                    }
                }
            }
        }
        Spacer(Modifier.height(18.dp))

        // Wortvorrat
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exercise.tiles.forEachIndexed { index, tile ->
                if (index !in interaction.chosenTiles) {
                    Tile(text = tile, enabled = !locked) {
                        interaction.chosenTiles.add(index)
                        onSpeak(tile, false)
                    }
                }
            }
        }
    }
}

@Composable
private fun Tile(text: String, enabled: Boolean, onClick: () -> Unit) {
    val sounds = LocalAppContainer.current.sounds
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier
            .padding(vertical = 4.dp)
            .pressScale(interactionSource)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled
            ) {
                sounds.tap()
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

/** Paare finden: türkisch ↔ deutsch. */
@Composable
fun MatchPairsView(
    exercise: Exercise.MatchPairs,
    interaction: ExerciseInteraction,
    onSpeak: (String, Boolean) -> Unit,
    onMismatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val turkish = remember(exercise) { exercise.items.shuffled() }
    val german = remember(exercise) { exercise.items.shuffled() }
    val matched = remember(exercise) { mutableStateOf(setOf<String>()) }
    var selectedTr by remember(exercise) { mutableStateOf<String?>(null) }
    var selectedDe by remember(exercise) { mutableStateOf<String?>(null) }
    var wrongPair by remember(exercise) { mutableStateOf(false) }

    LaunchedEffect(selectedTr, selectedDe) {
        val tr = selectedTr
        val de = selectedDe
        if (tr != null && de != null) {
            if (tr == de) {
                matched.value = matched.value + tr
                interaction.matchedCount = matched.value.size
            } else {
                wrongPair = true
                onMismatch()
                kotlinx.coroutines.delay(450)
                wrongPair = false
            }
            selectedTr = null
            selectedDe = null
        }
    }

    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            turkish.forEach { item ->
                val done = item.id in matched.value
                OptionCard(
                    text = item.tr,
                    status = when {
                        done -> OptionStatus.CORRECT
                        wrongPair && selectedTr == item.id -> OptionStatus.WRONG
                        selectedTr == item.id -> OptionStatus.SELECTED
                        else -> OptionStatus.NORMAL
                    },
                    enabled = !done
                ) {
                    selectedTr = item.id
                    onSpeak(item.tr, false)
                }
            }
        }
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            german.forEach { item ->
                val done = item.id in matched.value
                OptionCard(
                    text = item.de,
                    status = when {
                        done -> OptionStatus.CORRECT
                        wrongPair && selectedDe == item.id -> OptionStatus.WRONG
                        selectedDe == item.id -> OptionStatus.SELECTED
                        else -> OptionStatus.NORMAL
                    },
                    enabled = !done
                ) {
                    selectedDe = item.id
                }
            }
        }
    }

    if (matched.value.size == exercise.items.size) {
        Text(
            "Alle Paare gefunden! 🎉",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.GreenDark,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}
