package org.app.presentation.onboarding.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.data.repository.api.UserRepository
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _state = MutableStateFlow(
            SignUpContract.State(nickname = savedStateHandle.get<String>("nickname") ?: ""),
        )
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<SignUpContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun onEvent(event: SignUpContract.Event) {
            when (event) {
                is SignUpContract.Event.OnNicknameChanged -> {
                    _state.update { it.copy(nickname = event.value, nicknameError = null) }
                }

                SignUpContract.Event.OnNicknameNext -> {
                    val nickname = _state.value.nickname
                    when {
                        nickname.isBlank() -> _state.update {
                            it.copy(nicknameError = "닉네임을 입력해주세요")
                        }

                        nickname.length < 2 -> _state.update {
                            it.copy(nicknameError = "닉네임은 2자 이상 입력해주세요")
                        }

                        nickname.length > 20 -> _state.update {
                            it.copy(nicknameError = "닉네임은 20자 이내로 입력해주세요")
                        }

                        else -> viewModelScope.launch {
                            _sideEffect.emit(
                                SignUpContract.SideEffect.NavigateToTeamSelection(_state.value.nickname),
                            )
                        }
                    }
                }

                is SignUpContract.Event.OnTeamToggled -> {
                    _state.update { current ->
                        val ids = current.selectedTeamIds.toMutableSet()
                        if (event.teamId in ids) {
                            ids.remove(event.teamId)
                        } else if (current.canSelectMoreTeams) {
                            ids.add(event.teamId)
                        }
                        current.copy(selectedTeamIds = ids)
                    }
                }

                SignUpContract.Event.OnTeamSelectionConfirm -> {
                    submitOnboarding()
                }

                SignUpContract.Event.OnTeamSelectionSkip -> {
                    submitOnboarding(skipTeams = true)
                }

                SignUpContract.Event.OnBack -> {
                    viewModelScope.launch {
                        _sideEffect.emit(SignUpContract.SideEffect.NavigateBack)
                    }
                }
            }
        }

        private fun submitOnboarding(skipTeams: Boolean = false) {
            if (_state.value.isLoading) return
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val nickname = _state.value.nickname.trim()
                val teamIds = if (skipTeams) emptyList() else _state.value.selectedTeamIds.map { it.toLong() }

                userRepository
                    .postOnboarding(nickname = nickname, teamIds = teamIds)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false) }
                        _sideEffect.emit(SignUpContract.SideEffect.NavigateToComplete)
                    }.onFailure { error ->
                        _state.update { it.copy(isLoading = false) }
                        _sideEffect.emit(
                            SignUpContract.SideEffect.ShowToast(
                                error.message ?: "온보딩 처리 중 오류가 발생했어요",
                            ),
                        )
                    }
            }
        }
    }
