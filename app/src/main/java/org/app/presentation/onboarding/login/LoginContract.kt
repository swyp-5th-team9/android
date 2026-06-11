package org.app.presentation.onboarding.login

sealed interface LoginContract {
    sealed interface SideEffect {
        data object NavigateToHome : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }

    enum class SocialType {
        KAKAO,
        NAVER,
    }
}
