package org.app.presentation.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity

/**
 * [진단용/임시] 폰트 크기 이슈 원인 특정을 위한 로그.
 *
 * 기기 글꼴/화면 크기를 "기본값"으로 둬도 특정 폰에서만 텍스트가 크게 보이는 문제를
 * 수치로 확인하기 위한 코드다. 일반 화면과 바텀시트(별도 윈도우) 두 곳에서 호출해
 * 두 폰의 값을 비교하면 원인을 특정할 수 있다.
 *
 * 확인 항목:
 * - fontScale       : 시스템 글꼴 크기 설정 (기본 1.0). OEM 기본이 1.0 초과인 경우 발견 가능.
 * - appliedFontScale: MoballTheme의 fontScale 상한(1.3)이 실제 적용된 값. 상한이 이 화면까지
 *                     닿았는지 확인. NORMAL과 SHEET 값이 다르면 바텀시트에 상한 전파 실패.
 * - screenWidthDp   : 화면 폭(dp). 디자인 기준 412보다 작으면 고정 프레임 대비 상대적으로 커 보임.
 * - densityDpi      : 화면 밀도. 화면 크기(디스플레이 크기) 설정/기기 밀도 차이 확인.
 *
 * 원인 특정 후 이 파일과 호출부(HomeRoute, FilterBottomSheetContent)를 제거한다.
 */
@Composable
fun ScaleDiagnostics(tag: String) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    LaunchedEffect(tag, configuration.fontScale, configuration.densityDpi, configuration.screenWidthDp) {
        Log.d(
            "ScaleDiagnostics",
            "[$tag] fontScale=${configuration.fontScale}, " +
                "appliedFontScale=${density.fontScale}, " +
                "screenWidthDp=${configuration.screenWidthDp}, " +
                "densityDpi=${configuration.densityDpi}, " +
                "density=${density.density}",
        )
    }
}
