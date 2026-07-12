package org.app.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup

/**
 * [진단용/임시] 폰트 크기 이슈 원인 특정 오버레이.
 *
 * 문제가 관측되는 기기(예: 갤럭시 S25)를 직접 만질 수 없어 logcat을 못 볼 때를 대비해,
 * 값을 화면 위에 직접 오버레이로 띄운다. 그 기기 사용자가 스크린샷만 찍어 보내주면
 * 아래 값들을 바로 읽어 원인을 특정할 수 있다. (logcat도 병행 출력)
 *
 * 확인 항목:
 * - appliedFontScale: MoballTheme의 fontScale 상한(1.3)이 실제 적용된 값. 이 화면까지 상한이
 *                     닿았는지 확인. NORMAL과 SHEET 값이 다르면 바텀시트에 상한 전파 실패.
 * - fontScale       : 시스템 글꼴 크기 설정 원본(기본 1.0). OEM 기본이 1.0 초과인 경우 발견 가능.
 * - widthDp         : 화면 폭(dp). 디자인 기준 412보다 작으면 고정 프레임 대비 상대적으로 커 보임.
 * - dpi             : 화면 밀도. 화면 크기(디스플레이 크기) 설정/기기 밀도 차이 확인.
 *
 * 원인 특정 후 이 파일과 호출부(HomeRoute, FilterBottomSheetContent)를 제거한다.
 */
@Composable
fun ScaleDiagnostics(tag: String) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val text = "[$tag] appliedFontScale=${density.fontScale} · fontScale=${configuration.fontScale} · " +
        "widthDp=${configuration.screenWidthDp} · dpi=${configuration.densityDpi} · density=${density.density}"

    LaunchedEffect(text) { Log.d("ScaleDiagnostics", text) }

    // 화면 상단에 겹쳐 표시 — 스크린샷만으로 값 확인 가능. NORMAL/SHEET가 겹치지 않게 오프셋 분리.
    val yOffsetPx = with(density) { (if (tag == "SHEET") 96.dp else 56.dp).roundToPx() }
    Popup(alignment = Alignment.TopCenter, offset = IntOffset(0, yOffsetPx)) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 9.sp,
            modifier = Modifier
                .background(Color(0xCC000000))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
