package com.saittan.turkischlernen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saittan.turkischlernen.audio.AudioManager
import com.saittan.turkischlernen.audio.TtsStatus
import com.saittan.turkischlernen.data.repository.ProgressRepository
import com.saittan.turkischlernen.data.repository.WordRepository
import com.saittan.turkischlernen.ui.components.TtsMissingDialog
import com.saittan.turkischlernen.ui.screens.HomeScreen
import com.saittan.turkischlernen.ui.screens.ListeningGameScreen
import com.saittan.turkischlernen.ui.screens.SituationsScreen
import com.saittan.turkischlernen.ui.screens.WordCategoriesScreen
import com.saittan.turkischlernen.ui.screens.WordLearningScreen
import com.saittan.turkischlernen.ui.theme.TurkischLernenTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TurkischLernenTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    val audio = remember { AudioManager(context) }
    val progress = remember { ProgressRepository(context) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose { audio.shutdown() }
    }

    val ttsStatus by audio.status.collectAsState()
    var dialogDismissed by rememberSaveable { mutableStateOf(false) }
    if (ttsStatus == TtsStatus.TurkishMissing && !dialogDismissed) {
        TtsMissingDialog(onDismiss = { dialogDismissed = true })
    }

    val learnedWords by progress.learnedWords.collectAsState(initial = emptySet())

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                totalWords = WordRepository.totalWordCount,
                learnedCount = learnedWords.size.coerceAtMost(WordRepository.totalWordCount),
                onWordsClick = { navController.navigate(Routes.WORD_CATEGORIES) },
                onSituationsClick = { navController.navigate(Routes.SITUATIONS) },
                onGameClick = { navController.navigate(Routes.GAME) },
            )
        }
        composable(Routes.WORD_CATEGORIES) {
            WordCategoriesScreen(
                categories = WordRepository.categories,
                learnedWords = learnedWords,
                onBack = { navController.popBackStack() },
                onCategoryClick = { categoryId ->
                    navController.navigate("${Routes.WORD_LEARNING}/$categoryId")
                },
            )
        }
        composable("${Routes.WORD_LEARNING}/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId").orEmpty()
            val category = WordRepository.categoryById(categoryId)
            if (category != null) {
                WordLearningScreen(
                    category = category,
                    audio = audio,
                    learnedWords = learnedWords,
                    onCorrect = { word ->
                        scope.launch { progress.markLearned(category.id, word.turkish) }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
        }
        composable(Routes.SITUATIONS) {
            SituationsScreen(
                audio = audio,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.GAME) {
            ListeningGameScreen(
                audio = audio,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

private object Routes {
    const val HOME = "home"
    const val WORD_CATEGORIES = "words"
    const val WORD_LEARNING = "wordsLearning"
    const val SITUATIONS = "situations"
    const val GAME = "game"
}
