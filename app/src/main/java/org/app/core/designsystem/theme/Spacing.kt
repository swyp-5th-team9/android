package org.app.core.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * 공용 여백/치수 스케일.
 *
 * 흩어진 `N.dp` 매직 넘버를 하나로 모으기 위한 토큰. 사용 빈도가 높은 값(16/8/12/24/20/4…) 기준으로 정의한다.
 * 기존 코드의 530여 개 `dp` 리터럴을 한 번에 치환하지는 않고, **신규/수정 코드부터 점진 적용**한다.
 *
 * 예) `Modifier.padding(MoballSpacing.lg)` == `Modifier.padding(16.dp)`
 */
object MoballSpacing {
    /** 2.dp */
    val xxs = 2.dp

    /** 4.dp */
    val xs = 4.dp

    /** 8.dp */
    val sm = 8.dp

    /** 12.dp */
    val md = 12.dp

    /** 16.dp — 기본 화면 좌우 패딩 */
    val lg = 16.dp

    /** 20.dp */
    val xl = 20.dp

    /** 24.dp */
    val xxl = 24.dp

    /** 32.dp */
    val xxxl = 32.dp

    /** 40.dp */
    val huge = 40.dp
}
