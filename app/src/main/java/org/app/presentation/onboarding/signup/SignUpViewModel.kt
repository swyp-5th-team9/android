package org.app.presentation.onboarding.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.UserRepository
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel<SignUpContract.State, SignUpContract.Event, SignUpContract.SideEffect>(
            SignUpContract.State(nickname = savedStateHandle.get<String>("nickname") ?: ""),
        ) {
        override fun onEvent(event: SignUpContract.Event) {
            when (event) {
                is SignUpContract.Event.OnNicknameChanged -> {
                    setState { copy(nickname = event.value, nicknameError = null) }
                }

                SignUpContract.Event.OnNicknameNext -> {
                    val nickname = currentState.nickname.trim()
                    when {
                        nickname.isBlank() -> setState {
                            copy(nicknameError = "닉네임을 입력해주세요")
                        }

                        nickname.length < 2 -> setState {
                            copy(nicknameError = "닉네임은 2자 이상 입력해주세요")
                        }

                        nickname.length > 20 -> setState {
                            copy(nicknameError = "닉네임은 20자 이내로 입력해주세요")
                        }

                        else -> postSideEffect(SignUpContract.SideEffect.NavigateToTeamSelection(nickname))
                    }
                }

                is SignUpContract.Event.OnTeamToggled -> {
                    setState {
                        val ids = selectedTeamIds.toMutableSet()
                        if (event.teamId in ids) {
                            ids.remove(event.teamId)
                        } else if (canSelectMoreTeams) {
                            ids.add(event.teamId)
                        }
                        copy(selectedTeamIds = ids)
                    }
                }

                SignUpContract.Event.OnTeamSelectionConfirm -> {
                    submitOnboarding()
                }

                SignUpContract.Event.OnTeamSelectionSkip -> {
                    submitOnboarding(skipTeams = true)
                }

                SignUpContract.Event.OnBack -> {
                    postSideEffect(SignUpContract.SideEffect.NavigateBack)
                }
            }
        }

        private fun submitOnboarding(skipTeams: Boolean = false) {
            if (currentState.isLoading) return
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                val nickname = currentState.nickname.trim()
                val teamIds = if (skipTeams) emptyList() else currentState.selectedTeamIds.map { it.toLong() }

                userRepository
                    .postOnboarding(nickname = nickname, teamIds = teamIds)
                    .onSuccess {
                        setState { copy(isLoading = false) }
                        postSideEffect(SignUpContract.SideEffect.NavigateToComplete)
                    }.onFailure { error ->
                        setState { copy(isLoading = false) }
                        postSideEffect(
                            SignUpContract.SideEffect.ShowToast(
                                error.message ?: "온보딩 처리 중 오류가 발생했어요",
                            ),
                        )
                    }
            }
        }
    }
