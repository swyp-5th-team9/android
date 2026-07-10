package org.app.presentation.onboarding.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.AuthRepository
import org.app.domain.model.SocialType
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : BaseViewModel<LoginContract.State, LoginContract.Event, LoginContract.SideEffect>(
            LoginContract.State(),
        ) {
        override fun onEvent(event: LoginContract.Event) {
            when (event) {
                is LoginContract.Event.OnSocialTokenReceived ->
                    loginToServer(type = event.type, token = event.token)

                is LoginContract.Event.OnSocialLoginFailed -> {
                    Timber.e(event.throwable, "[Login] ${event.type.name} SDK 로그인 실패")
                    postSideEffect(
                        LoginContract.SideEffect.ShowToast("${event.type.name} 로그인에 실패했습니다."),
                    )
                }
            }
        }

        private fun loginToServer(
            type: SocialType,
            token: String,
        ) {
            if (currentState.isLoading) return
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                val loginResult =
                    when (type) {
                        SocialType.KAKAO -> authRepository.postKakaoLogin(authorization = token)
                        SocialType.NAVER -> authRepository.postNaverLogin(authorization = token)
                    }

                loginResult
                    .onSuccess { socialLoginToken ->
                        setState { copy(isLoading = false) }
                        // 탈퇴 후 30일 이내 재로그인으로 계정이 복구된 경우 안내
                        if (socialLoginToken.restored == true) {
                            postSideEffect(
                                LoginContract.SideEffect.ShowToast("다시 만나서 반가워요! 탈퇴 전 계정이 복구되었어요."),
                            )
                        }
                        if (socialLoginToken.onboardingCompleted == true) {
                            postSideEffect(LoginContract.SideEffect.NavigateToHome)
                        } else {
                            postSideEffect(LoginContract.SideEffect.NavigateToSignUp)
                        }
                    }.onFailure { error ->
                        Timber.e(error, "[Login] 서버 로그인 실패")
                        setState { copy(isLoading = false) }
                        postSideEffect(LoginContract.SideEffect.ShowToast("로그인에 실패했습니다. 잠시 후 다시 시도해주세요."))
                    }
            }
        }
    }
