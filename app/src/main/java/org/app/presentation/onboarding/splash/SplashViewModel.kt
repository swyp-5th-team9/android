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
                val isLoggedInDeferred = async { authRepository.isLoggedIn() }
                delay(SPLASH_DELAY_MS)
                val loginResult: Result<Boolean> = isLoggedInDeferred.await()
                val destination = if (loginResult.getOrElse { false }) {
                    SplashContract.SideEffect.NavigateToHome
                } else {
                    SplashContract.SideEffect.NavigateToLogin
                }
                _sideEffect.emit(destination)
            }
        }
    }
