package org.app.presentation.onboarding.signup

sealed interface OnboardingContract {
    data object State

    sealed interface SideEffect
}
