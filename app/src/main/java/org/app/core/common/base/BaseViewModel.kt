package org.app.core.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Contract(State/Event/SideEffect) 기반 ViewModel의 공통 구현.
 *
 * - [state]: UI 상태 단일 소스. [setState]로만 갱신합니다.
 * - [sideEffect]: 일회성 이벤트. [Channel] 기반이므로 구독자가 없어도 유실되지 않고,
 *   구독 시점에 순서대로 전달됩니다. (MutableSharedFlow의 emit 유실 문제 해결)
 * - 모든 UI 입력은 [onEvent] 단일 진입점으로 받습니다. Event를 우회하는
 *   public 함수를 추가하지 마세요.
 */
abstract class BaseViewModel<STATE : Any, EVENT : Any, SIDE_EFFECT : Any>(
    initialState: STATE,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _sideEffect = Channel<SIDE_EFFECT>(Channel.BUFFERED)
    val sideEffect: Flow<SIDE_EFFECT> = _sideEffect.receiveAsFlow()

    protected val currentState: STATE
        get() = _state.value

    abstract fun onEvent(event: EVENT)

    protected fun setState(reduce: STATE.() -> STATE) {
        _state.update(reduce)
    }

    protected fun postSideEffect(effect: SIDE_EFFECT) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}
