package org.app.presentation.mypage

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import org.app.core.common.state.UiState
import org.app.data.model.DummyUser

interface MyPageContract {
    @Immutable
    data class State(
        val dummyUsersLoadState: UiState<ImmutableList<DummyUser>> = UiState.Idle,
    )

    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
