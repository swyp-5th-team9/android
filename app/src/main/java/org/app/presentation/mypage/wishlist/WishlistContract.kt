package org.app.presentation.mypage.wishlist

interface WishlistContract {
    data class State(
        val items: List<WishlistItem> = emptyList(),
        val isLoading: Boolean = false,
        val isEditMode: Boolean = false,
        val selectedIds: Set<Long> = emptySet(), // favoriteId 기준
    ) {
        val hasSelection: Boolean get() = selectedIds.isNotEmpty()
    }

    sealed interface Event {
        data object OnEditClick : Event

        data object OnCancelEdit : Event

        data object OnDeleteSelected : Event

        data class OnToggleSelect(
            val favoriteId: Long,
        ) : Event

        data class OnPubClick(
            val pubId: Long,
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
    val favoriteId: Long,
    val pubId: Long,
    val pubName: String?,
    val address: String? = null,
    val thumbnailImageUrl: String? = null,
)
