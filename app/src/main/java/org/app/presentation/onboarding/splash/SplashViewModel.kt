package org.app.presentation.onboarding.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.app.data.repository.api.AuthRepository
import javax.inject.Inject

private const val SPLASH_DELAY_MS = 3000L

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        // Channel 기반: init 블록에서 send하는 시점에 구독자가 없어도 유실되지 않는다.
        private val _sideEffect = Channel<SplashContract.SideEffect>(Channel.BUFFERED)
        val sideEffect: Flow<SplashContract.SideEffect> = _sideEffect.receiveAsFlow()

        init {
            viewModelScope.launch {
                val isLoggedInDeferred = async { authRepository.isLoggedIn() }
                delay(SPLASH_DELAY_MS)
                val loginResult: Result<Boolean> = isLoggedInDeferred.await()
                val destination = if (loginResult.getOrElse { false }) {
                    SplashContract.SideEffect.NavigateToHome
                } else {
                    SplashContract.SideEffect.NavigateToLogin
                }
                _sideEffect.send(destination)
            }
        }
    }
