package org.app.presentation.home

interface HomeContract {
    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect
    }
}
