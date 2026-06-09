package org.app.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        private val _sideEffect = MutableSharedFlow<MyPageContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun logout() {
            viewModelScope.launch {
                authRepository
                    .logoutKakao()
                    .onSuccess {
                        _sideEffect.emit(MyPageContract.SideEffect.ShowToast("로그아웃 성공"))
                        _sideEffect.emit(MyPageContract.SideEffect.NavigateToLogin)
                        Timber.d("로그아웃 성공")
                    }.onFailure { error ->
                        _sideEffect.emit(MyPageContract.SideEffect.ShowToast("로그아웃 실패: ${error.message}"))
                        Timber.e("로그아웃 실패: $error")
                    }
            }
        }

        fun withdraw() {
            viewModelScope.launch {
                authRepository
                    .withdrawKakao()
                    .onSuccess {
                        _sideEffect.emit(MyPageContract.SideEffect.ShowToast("회원 탈퇴 성공"))
                        _sideEffect.emit(MyPageContract.SideEffect.NavigateToLogin)
                        Timber.d("회원 탈퇴 성공")
                    }.onFailure { error ->
                        _sideEffect.emit(MyPageContract.SideEffect.ShowToast("회원 탈퇴 실패: ${error.message}"))
                        Timber.e("회원 탈퇴 실패: $error")
                    }
            }
        }
    }
