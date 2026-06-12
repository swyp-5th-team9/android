package org.app.presentation.home.pubdetail

sealed interface PubDetailContract {
    data object State

    sealed interface SideEffect
}
