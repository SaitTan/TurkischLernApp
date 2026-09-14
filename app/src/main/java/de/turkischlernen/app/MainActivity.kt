package de.turkischlernen.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import de.turkischlernen.app.ui.navigation.AppRoot
import de.turkischlernen.app.ui.theme.TuerkischLernenTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as TurkischApp).container

        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                TuerkischLernenTheme {
                    AppRoot()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Sprachausgabe beim Verlassen der App beenden.
        (application as TurkischApp).container.speech.stop()
    }
}
