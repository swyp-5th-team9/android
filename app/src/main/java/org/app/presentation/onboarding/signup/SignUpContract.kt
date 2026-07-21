package org.app.presentation.onboarding.signup

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

interface SignUpContract {
    data class State(
        val nickname: String = "",
        val selectedTeamIds: ImmutableSet<Int> = persistentSetOf(),
        val isLoading: Boolean = false,
        val nicknameError: String? = null,
    ) {
        val isNicknameValid: Boolean
            get() = nickname.length in 2..20 && nicknameError == null
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
