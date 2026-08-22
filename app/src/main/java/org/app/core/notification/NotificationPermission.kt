package org.app.core.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Android 13(API 33)+ 에서 POST_NOTIFICATIONS 런타임 권한을 1회 요청하는 이펙트.
 *
 * 진입 화면(예: [org.app.presentation.main.MainScreen])에 배치한다.
 * 이미 허용됐거나 API 33 미만이면 아무 동작도 하지 않는다.
 * (필요 시 로그인 이후 시점으로 옮겨 UX를 개선할 수 있다.)
 *
 * 요청 여부는 [rememberSaveable]로 보존하므로, 구성 변경(회전 등)이나 프로세스 사망으로
 * 화면이 재생성돼도 권한 요청이 중복으로 뜨지 않는다.
 */
@Composable
fun RequestNotificationPermissionEffect() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    var hasRequested by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* 결과는 시스템 알림 표시 여부에만 영향 — 별도 처리 불필요 */ }

    LaunchedEffect(Unit) {
        if (hasRequested) return@LaunchedEffect
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            hasRequested = true
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
