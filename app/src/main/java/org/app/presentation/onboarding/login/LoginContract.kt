package org.app.presentation.onboarding.login

import org.app.domain.model.SocialType

interface LoginContract {
    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        /** 소셜 SDK 로그인 성공 — 발급받은 토큰을 서버로 전달 */
        data class OnSocialTokenReceived(
            val type: SocialType,
            val token: String,
        ) : Event

        /** 소셜 SDK 로그인 실패 */
        data class OnSocialLoginFailed(
            val type: SocialType,
            val throwable: Throwable,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateToHome : SideEffect

        data object NavigateToSignUp : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
