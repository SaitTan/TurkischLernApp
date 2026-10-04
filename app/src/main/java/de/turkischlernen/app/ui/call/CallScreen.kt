package de.turkischlernen.app.ui.call

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.content.CallOption
import de.turkischlernen.app.data.content.CallScript
import de.turkischlernen.app.data.content.CallScripts
import de.turkischlernen.app.data.content.SpeechMatch
import de.turkischlernen.app.ui.components.ChunkyButton
import de.turkischlernen.app.ui.components.ConfettiOverlay
import de.turkischlernen.app.ui.mascot.Kangal
import de.turkischlernen.app.ui.mascot.MascotMood
import de.turkischlernen.app.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Eine Sprechblase im Gesprächsverlauf. */
private data class Bubble(val tr: String, val de: String, val vomPartner: Boolean)

/**
 * Telefonat mit dem Kangal: Er spricht, das Kind antwortet laut oder per Auswahl.
 * Die Gespräche sind fest geschrieben – keine KI, kein Internet nötig.
 */
@Composable
fun CallScreen(
    script: CallScript,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val settings by container.settingsRepository.current.collectAsState()
    val context = LocalContext.current
    val listener = container.speechListener
    val scope = rememberCoroutineScope()

    val verlauf = remember { mutableStateListOf<Bubble>() }
    var currentId by remember { mutableStateOf(script.startId) }
    var zuhoeren by remember { mutableStateOf(false) }
    var zeigeAuswahl by remember {
        mutableStateOf(!listener.available || !settings.speechRecognition)
    }
    var beendet by remember { mutableStateOf(false) }
    val listenState = rememberLazyListState()

    val aktuelleZeile = script.line(currentId)

    val pulse by rememberInfiniteTransition(label = "callMic").animateFloat(
        initialValue = 1f,
        targetValue = if (zuhoeren) 1.14f else 1f,
        animationSpec = infiniteRepeatable(tween(520), RepeatMode.Reverse),
        label = "callMicPuls"
    )

    // Der Kangal sagt seinen Satz, sobald eine neue Zeile dran ist.
    LaunchedEffect(currentId) {
        val zeile = script.line(currentId) ?: return@LaunchedEffect
        verlauf += Bubble(zeile.partnerTr, zeile.partnerDe, vomPartner = true)
        container.speech.speak(zeile.partnerTr, slow = false)
        if (zeile.options.isEmpty()) {
            delay(600)
            beendet = true
            container.sounds.celebrate()
            scope.launch {
                container.progressRepository.addBonusXp(CallScripts.XP_REWARD)
                container.progressRepository.addPlayMinutes(CallScripts.PLAY_MINUTES_REWARD)
            }
        }
    }

    LaunchedEffect(verlauf.size) {
        if (verlauf.isNotEmpty()) listenState.animateScrollToItem(verlauf.lastIndex)
    }

    fun antworten(option: CallOption) {
        verlauf += Bubble(option.tr, option.de, vomPartner = false)
        zeigeAuswahl = !listener.available || !settings.speechRecognition
        val ziel = option.nextId
        if (ziel == null) {
            beendet = true
            container.sounds.celebrate()
            scope.launch {
                container.progressRepository.addBonusXp(CallScripts.XP_REWARD)
                container.progressRepository.addPlayMinutes(CallScripts.PLAY_MINUTES_REWARD)
            }
        } else {
            currentId = ziel
        }
    }

    fun auswerten(gehoert: String?) {
        zuhoeren = false
        val zeile = script.line(currentId) ?: return
        if (gehoert.isNullOrBlank()) {
            zeigeAuswahl = true
            return
        }
        val treffer = zeile.options
            .maxByOrNull { SpeechMatch.similarity(gehoert, it.tr) }
            ?.takeIf { SpeechMatch.matches(gehoert, it.tr) }

        if (treffer != null) antworten(treffer) else zeigeAuswahl = true
    }

    val erlaubnisStarter = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { erlaubt ->
        if (erlaubt) {
            zuhoeren = true
            listener.start { auswerten(it) }
        } else {
            zeigeAuswahl = true
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Kopfzeile wie bei einem Anruf
        Row(
            Modifier
                .fillMaxWidth()
                .background(AppColors.Green)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Kangal(
                mood = if (beendet) MascotMood.CHEER else MascotMood.IDLE,
                size = 64.dp,
                avatar = settings.avatar
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    script.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    if (beendet) "Gespräch beendet" else "Telefonat läuft …",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Text("📞", fontSize = 26.sp)
        }

        LazyColumn(
            state = listenState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(verlauf) { bubble ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = if (bubble.vomPartner) Arrangement.Start else Arrangement.End
                ) {
                    Column(
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (bubble.vomPartner) MaterialTheme.colorScheme.surfaceVariant
                                else AppColors.BlueLight
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            bubble.tr,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (bubble.vomPartner) MaterialTheme.colorScheme.onBackground
                            else AppColors.BlueDark
                        )
                        Text(
                            bubble.de,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                beendet -> {
                    Text(
                        "Super gemacht! +${CallScripts.XP_REWARD} XP und " +
                            "+${CallScripts.PLAY_MINUTES_REWARD} Minute PlayStation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.GreenDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    ChunkyButton(
                        text = "AUFLEGEN",
                        modifier = Modifier.fillMaxWidth(),
                        color = AppColors.Red
                    ) { onClose() }
                }

                aktuelleZeile == null -> {
                    ChunkyButton(
                        text = "AUFLEGEN",
                        modifier = Modifier.fillMaxWidth(),
                        color = AppColors.Red
                    ) { onClose() }
                }

                else -> {
                    if (!zeigeAuswahl) {
                        Box(
                            Modifier
                                .size(96.dp)
                                .scale(pulse)
                                .clip(CircleShape)
                                .background(if (zuhoeren) AppColors.Red else AppColors.Blue)
                                .clickable(enabled = !zuhoeren) {
                                    val erlaubt = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (erlaubt) {
                                        zuhoeren = true
                                        listener.start { auswerten(it) }
                                    } else {
                                        erlaubnisStarter.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎤", fontSize = 44.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (zuhoeren) "Ich höre zu …" else "Antworte laut auf Türkisch",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Antworten anzeigen",
                            style = MaterialTheme.typography.labelMedium,
                            color = AppColors.Blue,
                            modifier = Modifier.clickable { zeigeAuswahl = true }
                        )
                    } else {
                        aktuelleZeile.options.forEach { option ->
                            ChunkyButton(
                                text = option.tr,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                color = AppColors.Blue
                            ) { antworten(option) }
                        }
                        Text(
                            aktuelleZeile.options.joinToString("  ·  ") { it.de },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (beendet) ConfettiOverlay()
}
