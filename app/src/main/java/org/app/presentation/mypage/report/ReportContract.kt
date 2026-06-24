package org.app.presentation.mypage.report

interface ReportContract {
    data class State(
        val selectedCategory: ReportCategory = ReportCategory.PUB_INFO,
        val detailText: String = "",
        val screenshots: List<String> = emptyList(), // URI strings
        val isSubmitting: Boolean = false,
    ) {
        val canSubmit: Boolean get() = detailText.isNotBlank()
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
) {
    PUB_INFO("펍 정보"),
    APP_ERROR("앱 오류"),
    ETC("기타"),
}
