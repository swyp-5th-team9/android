package org.app.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import org.app.core.designsystem.theme.Swyp_5th_team9Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Swyp_5th_team9Theme {
                val appState = rememberMainAppState()

                MainScreen(
                    appState = appState,
                )
            }
        }
    }
}
