package org.app.presentation.onboarding.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.app.data.repository.api.AuthRepository
import javax.inject.Inject

private const val SPLASH_DELAY_MS = 2000L

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _sideEffect = MutableSharedFlow<SplashContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            viewModelScope.launch {
                // 토큰 확인과 최소 표시 시간을 병렬로 실행
                val isLoggedInDeferred = async { authRepository.isLoggedIn() }
                delay(SPLASH_DELAY_MS)
                val isLoggedIn = isLoggedInDeferred.await()
                if (isLoggedIn) {
                    _sideEffect.emit(SplashContract.SideEffect.NavigateToHome)
                } else {
                    _sideEffect.emit(SplashContract.SideEffect.NavigateToLogin)
                }
            }
        }
    }
