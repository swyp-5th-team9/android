package org.app.presentation.mypage

interface MyPageContract {
    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
