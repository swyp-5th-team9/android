package org.app.presentation.home

sealed interface HomeContract {
    data object State

    sealed interface SideEffect
}
