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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(MyPageContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<MyPageContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

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

                MyPageContract.Event.OnTeamSelectDismiss -> {
                    Unit
                }
            }
        }

        fun logout() {
            viewModelScope.launch {
                authRepository
                    .logout()
                    .onSuccess {
                        emit(MyPageContract.SideEffect.ShowToast("로그아웃 성공"))
                        emit(MyPageContract.SideEffect.NavigateToLogin)
                        Timber.d("로그아웃 성공")
                    }.onFailure { error ->
                        emit(MyPageContract.SideEffect.ShowToast("로그아웃 실패: ${error.message}"))
                        Timber.e("로그아웃 실패: $error")
                    }
            }
        }

        fun withdraw() {
            viewModelScope.launch {
                authRepository
                    .withdraw()
                    .onSuccess {
                        emit(MyPageContract.SideEffect.ShowToast("회원 탈퇴 성공"))
                        emit(MyPageContract.SideEffect.NavigateToLogin)
                        Timber.d("회원 탈퇴 성공")
                    }.onFailure { error ->
                        emit(MyPageContract.SideEffect.ShowToast("회원 탈퇴 실패: ${error.message}"))
                        Timber.e("회원 탈퇴 실패: $error")
                    }
            }
        }

        private fun emit(effect: MyPageContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
