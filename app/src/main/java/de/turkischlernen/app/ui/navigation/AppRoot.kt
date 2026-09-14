package de.turkischlernen.app.ui.navigation

import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.turkischlernen.app.LocalAppContainer
import de.turkischlernen.app.data.progress.UserProgress
import de.turkischlernen.app.ui.components.StatsBar
import de.turkischlernen.app.ui.lesson.LessonScreen
import de.turkischlernen.app.ui.lesson.LessonViewModel
import de.turkischlernen.app.ui.path.PathScreen
import de.turkischlernen.app.ui.practice.PracticeScreen
import de.turkischlernen.app.ui.profile.ProfileScreen
import de.turkischlernen.app.ui.situations.SituationsScreen
import kotlinx.coroutines.launch

private object Routes {
    const val HOME = "home"
    const val LESSON = "lesson/{lessonId}"
    fun lesson(lessonId: String) = "lesson/$lessonId"
}

@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    // Wörter für eine Wiederholungs-Session (wird vom Übungs-Tab gesetzt).
    var practiceItemIds by remember { mutableStateOf(emptyList<String>()) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                HomeScreen(
                    onStartLesson = { lessonId -> navController.navigate(Routes.lesson(lessonId)) },
                    onStartPractice = { ids ->
                        practiceItemIds = ids
                        navController.navigate(Routes.lesson(LessonViewModel.PRACTICE_ID))
                    }
                )
            }
            composable(Routes.LESSON) { entry ->
                val lessonId = entry.arguments?.getString("lessonId").orEmpty()
                LessonScreen(
                    lessonId = lessonId,
                    practiceItemIds = practiceItemIds,
                    onExit = { navController.popBackStack() }
                )
            }
        }
    }
}

/** Die vier Bereiche der App mit unterer Navigationsleiste. */
@Composable
private fun HomeScreen(
    onStartLesson: (String) -> Unit,
    onStartPractice: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val progress by container.progressRepository.progress.collectAsState(initial = UserProgress())
    var tab by remember { mutableIntStateOf(0) }

    val speak: (String, Boolean) -> Unit = { text, slow -> container.speech.speak(text, slow) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                TABS.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = { Text(item.emoji, fontSize = 22.sp) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (tab == 0) StatsBar(progress)

            when (tab) {
                0 -> PathScreen(
                    progress = progress,
                    ttsWarning = !container.speech.turkishAvailable,
                    onLessonClick = { lesson -> onStartLesson(lesson.id) },
                    onLockedClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Schließe zuerst die Lektion davor ab 🔒"
                            )
                        }
                    },
                    onTtsWarningClick = {
                        runCatching {
                            context.startActivity(
                                Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                )

                1 -> SituationsScreen(onSpeak = speak)

                2 -> PracticeScreen(
                    progress = progress,
                    onSpeak = speak,
                    onPractice = onStartPractice
                )

                else -> ProfileScreen(progress = progress)
            }
        }
    }
}

private data class TabItem(val emoji: String, val label: String)

private val TABS = listOf(
    TabItem("🏠", "Lernen"),
    TabItem("🙋", "Ich brauche"),
    TabItem("🔁", "Üben"),
    TabItem("🦉", "Profil")
)
