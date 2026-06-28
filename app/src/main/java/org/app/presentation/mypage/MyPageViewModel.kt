package org.app.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.data.repository.api.AuthRepository
import org.app.data.repository.api.UserRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(MyPageContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<MyPageContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadUser()
        }

        fun refresh() {
            loadUser()
        }

        private fun loadUser() {
            if (_state.value.isLoading) return
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                userRepository
                    .getUser()
                    .onSuccess { user ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                nickname = user.nickname,
                                supportedTeams = user.favoriteTeams.map { team -> team.teamName },
                            )
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(isLoading = false) }
                        emit(MyPageContract.SideEffect.ShowToast("내 정보 조회에 실패했습니다."))
                        Timber.e("내 정보 조회 실패: $error")
                    }
            }
        }

        fun onEvent(event: MyPageContract.Event) {
            when (event) {
                MyPageContract.Event.OnEditProfileClick -> {
                    emit(MyPageContract.SideEffect.NavigateToEditProfile)
                }

                MyPageContract.Event.OnWishlistClick -> {
                    emit(MyPageContract.SideEffect.NavigateToWishlist)
                }

                MyPageContract.Event.OnReportClick -> {
                    emit(MyPageContract.SideEffect.NavigateToReport)
                }

                MyPageContract.Event.OnWithdrawClick -> {
                    emit(MyPageContract.SideEffect.NavigateToWithdraw)
                }

                MyPageContract.Event.OnLogoutClick -> {
                    logout()
                }

                MyPageContract.Event.OnAddTeamClick -> {
                    // bottom sheet open은 UI 레이어에서 처리
                }

                is MyPageContract.Event.OnTeamSelected -> {
                    val currentTeams = _state.value.supportedTeams
                    if (event.team in currentTeams) {
                        _state.update { it.copy(supportedTeams = it.supportedTeams - event.team) }
                    } else if (currentTeams.size < 3) {
                        _state.update { it.copy(supportedTeams = it.supportedTeams + event.team) }
                    } else {
                        emit(MyPageContract.SideEffect.ShowToast("응원 구단은 최대 3개까지 선택 가능합니다."))
                    }
                }

                MyPageContract.Event.OnTeamSelectDismiss -> { // no-op
                }

                MyPageContract.Event.OnCopyEmailClick -> {
                    emit(MyPageContract.SideEffect.ShowToast("이메일 주소가 복사되었습니다."))
                }

                is MyPageContract.Event.OnPubClick -> {
                    emit(MyPageContract.SideEffect.NavigateToPubDetail(event.pubId))
                }
            }
        }

        private fun logout() {
            viewModelScope.launch {
                authRepository
                    .logout()
                    .onSuccess {
                        emit(MyPageContract.SideEffect.NavigateToLogin)
                        Timber.d("로그아웃 성공")
                    }.onFailure { error ->
                        emit(MyPageContract.SideEffect.ShowToast("로그아웃 실패: ${error.message}"))
                        Timber.e("로그아웃 실패: $error")
                    }
            }
        }

        private fun emit(effect: MyPageContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
