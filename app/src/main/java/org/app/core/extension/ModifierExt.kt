package org.app.core.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * 리플 효과 없이 클릭 가능하게 만드는 Modifier
 *
 * Material3의 기본 리플 애니메이션을 제거합니다.
 *
 * @param onClick 클릭 시 실행될 콜백
 */

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit = {}): Modifier =
    composed {
        this.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
        ) {
            onClick()
        }
    }

/**
 * 접근성: 최소 48dp 터치 영역을 확보하는 Modifier
 *
 * 시각적 크기는 그대로 두고 터치/레이아웃 영역만 최소 48dp로 확장합니다.
 * (요소가 이미 48dp 이상이면 무동작)
 *
 * 반드시 클릭 요소의 **최외곽**(배경·모양·그림자 모디파이어보다 앞)에 적용해야
 * 배경이 커지지 않고 터치 영역만 넓어집니다. Material3 `IconButton`과 동일한 방식입니다.
 *
 * 예) `Modifier.minTouchTarget().background(...).noRippleClickable(onClick)`
 */
fun Modifier.minTouchTarget(): Modifier = minimumInteractiveComponentSize()
