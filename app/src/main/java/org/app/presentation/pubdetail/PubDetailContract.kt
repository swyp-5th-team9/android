package org.app.presentation.pubdetail

sealed interface PubDetailContract {
    data object State

    sealed interface SideEffect
}
