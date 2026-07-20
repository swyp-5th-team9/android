package org.app.presentation.main

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import org.app.core.designsystem.theme.MoballTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 내비게이션 바 스크림을 제거(투명)해 화면 배경색이 그대로 비치도록 한다.
        // (기본 enableEdgeToEdge는 3버튼 내비에 반투명 스크림을 씌워 배경색과 어긋남)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            MoballTheme {
                val appState = rememberMainAppState()

                MainScreen(
                    appState = appState,
                )
            }
        }
    }
}
