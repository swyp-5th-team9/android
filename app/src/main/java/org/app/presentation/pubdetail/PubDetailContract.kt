package org.app.presentation.pubdetail

import org.app.presentation.pubdetail.model.PubDetail

interface PubDetailContract {
    data class State(
        val pubId: String = "",
        val pubDetail: PubDetail? = null,
        val isLoading: Boolean = false,
        val favoriteId: Long? = null, // 찜 등록 시 서버에서 받은 ID (삭제에 사용)
        val isWishlistLoading: Boolean = false,
        val currentImageIndex: Int = 0,
        val isHoursExpanded: Boolean = false,
        val showPhotoGallery: Boolean = false,
        val selectedPhotoIndex: Int = 0,
    )

    sealed interface Event {
        data object OnBack : Event

        data object OnWishlistToggle : Event

        data class OnImagePageChanged(
            val index: Int,
        ) : Event

        data object OnPhoneCall : Event

        data object OnKakaoMapClick : Event

        data object OnNaverMapClick : Event

        data object OnHoursToggle : Event

        data class OnPhotoClick(
            val index: Int,
        ) : Event

        data object OnPhotoGalleryClose : Event

        data class OnPhotoGalleryPageChanged(
            val index: Int,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        data class CallPhone(
            val phoneNumber: String,
        ) : SideEffect

        data class OpenMap(
            val url: String,
            val appScheme: String,
            val webFallbackUrl: String,
        ) : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
