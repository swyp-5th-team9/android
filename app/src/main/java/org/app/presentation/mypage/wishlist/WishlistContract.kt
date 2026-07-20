package org.app.presentation.mypage.wishlist

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

interface WishlistContract {
    data class State(
        val items: ImmutableList<WishlistItem> = persistentListOf(),
        val isLoading: Boolean = false,
        val isEditMode: Boolean = false,
        val selectedIds: ImmutableSet<Long> = persistentSetOf(), // favoriteId 기준
    ) {
        val hasSelection: Boolean get() = selectedIds.isNotEmpty()
    }

    sealed interface Event {
        /** 화면 재진입(ON_RESUME) 시 목록 갱신 */
        data object OnRefresh : Event

        data object OnEditClick : Event

        data object OnCancelEdit : Event

        data object OnDeleteSelected : Event

        data class OnToggleSelect(
            val favoriteId: Long,
        ) : Event

        data class OnHeartClick(
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
