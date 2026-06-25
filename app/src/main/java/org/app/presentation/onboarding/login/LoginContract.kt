package org.app.presentation.onboarding.login

sealed interface LoginContract {
    sealed interface SideEffect {
        data object NavigateToHome : SideEffect

        data object NavigateToSignUp : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
