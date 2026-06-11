package org.app.presentation.onboarding.signup

sealed interface SignUpContract {
    data object State

    sealed interface SideEffect
}
