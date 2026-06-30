package org.app.presentation.onboarding.signup

interface SignUpContract {
    data class State(
        val nickname: String = "",
        val selectedTeamIds: Set<Int> = emptySet(),
        val isLoading: Boolean = false,
        val nicknameError: String? = null,
    ) {
        val isNicknameValid: Boolean get() = nickname.isNotBlank() && nickname.length <= 20
        val canSelectMoreTeams: Boolean get() = selectedTeamIds.size < 3
    }

    sealed interface Event {
        data class OnNicknameChanged(
            val value: String,
        ) : Event

        data object OnNicknameNext : Event

        data class OnTeamToggled(
            val teamId: Int,
        ) : Event

        data object OnTeamSelectionConfirm : Event

        data object OnTeamSelectionSkip : Event

        data object OnBack : Event
    }

    sealed interface SideEffect {
        data class NavigateToTeamSelection(
            val nickname: String,
        ) : SideEffect

        data object NavigateToComplete : SideEffect

        data object NavigateBack : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
