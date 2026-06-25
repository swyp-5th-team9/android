package org.app.presentation.mypage.wishlist

interface WishlistContract {
    data class State(
        val items: List<WishlistItem> = emptyList(),
        val isLoading: Boolean = false,
        val isEditMode: Boolean = false,
        val selectedIds: Set<String> = emptySet(),
    ) {
        val hasSelection: Boolean get() = selectedIds.isNotEmpty()
    }

    sealed interface Event {
        data object OnEditClick : Event

        data object OnCancelEdit : Event

        data object OnDeleteSelected : Event

        data class OnToggleSelect(
            val pubId: String,
        ) : Event

        data class OnToggleFavorite(
            val pubId: String,
        ) : Event

        data class OnPubClick(
            val pubId: String,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        data class NavigateToPubDetail(
            val pubId: String,
        ) : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}

data class WishlistItem(
    val pubId: String,
    val pubName: String,
    val location: String,
    val imageUrl: String? = null,
)
