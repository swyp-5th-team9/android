package org.app.presentation.mypage

interface MyPageContract {
    data class State(
        val nickname: String = "",
        val profileImageUrl: String? = null,
        val supportedTeams: List<String> = emptyList(),
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        data object OnEditProfileClick : Event

        data object OnSettingPrivacyClick : Event

        data object OnReportClick : Event

        data object OnWithdrawClick : Event

        data object OnAddTeamClick : Event

        data class OnTeamSelected(
            val team: String,
        ) : Event

        data object OnTeamSelectDismiss : Event
    }

    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect

        data object NavigateToEditProfile : SideEffect

        data object NavigateToReport : SideEffect

        data object NavigateToWithdraw : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
