package org.app.presentation.mypage.report

import android.net.Uri

interface ReportContract {
    data class State(
        val pubId: Long? = null,
        val selectedCategory: ReportCategory = ReportCategory.PUB_INFO,
        val detailText: String = "",
        val imageUris: List<Uri> = emptyList(),
        val isSubmitting: Boolean = false,
    ) {
        val canSubmit: Boolean get() = detailText.isNotBlank() && !isSubmitting
        val remainingImageSlots: Int get() = MAX_IMAGES - imageUris.size

        companion object {
            const val MAX_IMAGES = 3
            const val MAX_CONTENT_LENGTH = 500
        }
    }

    sealed interface Event {
        data class OnCategorySelected(
            val category: ReportCategory,
        ) : Event

        data class OnDetailTextChanged(
            val text: String,
        ) : Event

        data class OnImagesAdded(
            val uris: List<Uri>,
        ) : Event

        data class OnImageRemoved(
            val uri: Uri,
        ) : Event

        data object OnSubmit : Event

        data object OnBack : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        data object ShowSuccessDialog : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}

enum class ReportCategory(
    val label: String,
    /** 서버 전송 시 사용하는 API 값 */
    val apiValue: String,
) {
    PUB_INFO("펍 정보", "PUB_INFO"),
    APP_ERROR("앱 오류", "APP_ERROR"),
    ETC("기타", "OTHER"),
}
