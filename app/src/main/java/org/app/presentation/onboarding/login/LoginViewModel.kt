package org.app.presentation.onboarding.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.app.data.repository.api.AuthRepository
import org.app.domain.model.SocialType
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val loginManagers: Map<SocialType, @JvmSuppressWildcards SocialLoginManager>,
    ) : ViewModel() {
        private val _sideEffect = MutableSharedFlow<LoginContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun login(
            type: SocialType,
            context: Context,
        ) = viewModelScope.launch {
            val loginManager = loginManagers[type]
                ?: throw IllegalArgumentException("Unsupported social type: $type")

            Timber.d("Login type: $type, Manager: ${loginManager.javaClass.simpleName}")

            loginManager
                .login(context = context)
                .onSuccess { token ->
                    val loginResult =
                        when (type) {
                            SocialType.KAKAO -> authRepository.postKakaoLogin(authorization = token)
                            SocialType.NAVER -> authRepository.postNaverLogin(authorization = token)
                        }

                    loginResult
                        .onSuccess {
                            _sideEffect.emit(LoginContract.SideEffect.ShowToast("${type.name} 로그인 성공"))
                            _sideEffect.emit(LoginContract.SideEffect.NavigateToHome)
                        }.onFailure { error ->
                            _sideEffect.emit(LoginContract.SideEffect.ShowToast("서버 로그인 실패: ${error.message}"))
                        }
                }.onFailure { error ->
                    _sideEffect.emit(LoginContract.SideEffect.ShowToast("${type.name} 로그인 실패: ${error.message}"))
                }
        }
    }
