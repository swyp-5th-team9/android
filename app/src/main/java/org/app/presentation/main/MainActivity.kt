package org.app.presentation.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.notification.NotificationDeepLink

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // FCM 푸시 탭으로 실행/재진입 시 딥링크 요청. MainScreen이 소비 후 null로 비운다.
    private val pendingDeepLink = MutableStateFlow<NotificationDeepLink.Request?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 시스템 스플래시(로고 + #303339 배경) 설치. super.onCreate 이전에 호출해야 하며,
        // 이후 postSplashScreenTheme(Theme.Moball)로 자동 전환된다.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // 내비게이션 바 스크림을 제거(투명)해 화면 배경색이 그대로 비치도록 한다.
        // (기본 enableEdgeToEdge는 3버튼 내비에 반투명 스크림을 씌워 배경색과 어긋남)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        pendingDeepLink.value = NotificationDeepLink.fromIntent(intent)
        setContent {
            MoballTheme {
                val appState = rememberMainAppState()

                MainScreen(
                    appState = appState,
                    pendingDeepLink = pendingDeepLink,
                    onDeepLinkHandled = {
                        pendingDeepLink.value = null
                        NotificationDeepLink.clearExtras(intent)
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 앱이 이미 실행 중일 때 푸시 탭으로 재진입한 경우
        setIntent(intent)
        NotificationDeepLink.fromIntent(intent)?.let { pendingDeepLink.value = it }
    }
}
