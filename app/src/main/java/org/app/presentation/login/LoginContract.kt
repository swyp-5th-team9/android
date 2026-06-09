package org.app.presentation.login

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
