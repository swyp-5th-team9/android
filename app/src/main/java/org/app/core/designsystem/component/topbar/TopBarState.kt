package org.app.core.designsystem.component.topbar

sealed interface TopBarState {
    val title: String

    data class Default(
        override val title: String,
    ) : TopBarState

    data class Back(
        override val title: String,
        val onBackClick: () -> Unit,
    ) : TopBarState

    data class Close(
        override val title: String,
        val onCloseClick: () -> Unit,
    ) : TopBarState

    data class BackWithOnboarding1(
        override val title: String,
        val onBackClick: () -> Unit,
    ) : TopBarState

    data class BackWithTextMenu(
        override val title: String,
        val menuText: String,
        val onBackClick: () -> Unit,
        val onMenuClick: () -> Unit,
    ) : TopBarState

    data class BackWithSkip(
        override val title: String = "",
        val onBackClick: () -> Unit,
        val onSkipClick: () -> Unit,
    ) : TopBarState
}
