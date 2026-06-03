package org.app.presentation.home

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import org.app.core.common.state.UiState
import org.app.data.model.DummyUser

interface HomeContract {
    @Immutable
    data class State(
        val dummyUsersLoadState: UiState<ImmutableList<DummyUser>> = UiState.Idle,
    )
}
