package org.app.core.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * SideEffect Flow를 라이프사이클을 존중하며 수집합니다.
 *
 * [Lifecycle.State.STARTED] 이상에서만 수집하므로 화면이 백그라운드에 있는 동안
 * 토스트·내비게이션이 실행되는 것을 방지합니다.
 */
@Composable
fun <T> CollectSideEffect(
    sideEffect: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onEffect: suspend (T) -> Unit,
) {
    LaunchedEffect(sideEffect, lifecycleOwner, minActiveState) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            sideEffect.collect(onEffect)
        }
    }
}
