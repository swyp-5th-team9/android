package org.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.core.common.state.UiState
import org.app.data.model.DummyUser
import org.app.data.repository.api.DummyRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val dummyRepository: DummyRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(HomeContract.State())
        val uiState = _uiState.asStateFlow()

        fun fetchDummyUsers() =
            viewModelScope.launch {
                dummyRepository
                    .fetchDummyUserList(page = 2)
                    .onSuccess { userList ->
                        if (userList.isNotEmpty()) {
                            updateDummyUiState(UiState.Success(userList.toImmutableList()))
                        } else {
                            updateDummyUiState(UiState.Idle)
                        }
                    }.onFailure {
                        updateDummyUiState(UiState.Failure(it.message ?: "Unknown Error"))
                    }
            }

        private fun updateDummyUiState(value: UiState<ImmutableList<DummyUser>>) {
            _uiState.update { currentState ->
                currentState.copy(
                    dummyUsersLoadState = value,
                )
            }
        }
    }
