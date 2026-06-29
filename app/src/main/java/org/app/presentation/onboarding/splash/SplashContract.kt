package org.app.presentation.onboarding.splash

sealed interface SplashContract {
    sealed interface SideEffect {
        data object NavigateToHome : SideEffect

        data object NavigateToLogin : SideEffect
    }
}
