package org.app.presentation.login

sealed interface LoginContract {
    sealed interface SideEffect {
        data object NavigateToHome : SideEffect
    }
}
