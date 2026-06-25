package org.app.presentation.mypage

import org.app.presentation.mypage.wishlist.WishlistItem

interface MyPageContract {
    data class State(
        val nickname: String = "",
        val profileImageUrl: String? = null,
        val supportedTeams: List<String> = emptyList(),
        val wishlistItems: List<WishlistItem> = emptyList(),
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        data object OnEditProfileClick : Event

        data object OnWishlistClick : Event

        data object OnReportClick : Event

        data object OnWithdrawClick : Event

        data object OnLogoutClick : Event

        data object OnAddTeamClick : Event

        data class OnTeamSelected(
            val team: String,
        ) : Event

        data object OnTeamSelectDismiss : Event

        data object OnCopyEmailClick : Event

        data class OnPubClick(
            val pubId: String,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect

        data object NavigateToEditProfile : SideEffect

        data object NavigateToReport : SideEffect

        data object NavigateToWithdraw : SideEffect

        data object NavigateToWishlist : SideEffect

        data class NavigateToPubDetail(
            val pubId: String,
        ) : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
