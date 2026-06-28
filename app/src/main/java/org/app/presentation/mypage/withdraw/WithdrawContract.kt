package org.app.presentation.mypage.withdraw

interface WithdrawContract {
    data class State(
        val selectedReason: WithdrawReason? = null,
        val etcText: String = "",
        val isAgreed: Boolean = false,
        val isLoading: Boolean = false,
    ) {
        val canWithdraw: Boolean
            get() = selectedReason != null && isAgreed
    }

    sealed interface Event {
        data class OnReasonSelected(
            val reason: WithdrawReason,
        ) : Event

        data class OnEtcTextChanged(
            val text: String,
        ) : Event

        data object OnAgreementToggle : Event

        data object OnWithdrawClick : Event

        data object OnWithdrawConfirm : Event
    }

    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect

        data object NavigateBack : SideEffect

        data object ShowSuccessDialog : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}

enum class WithdrawReason(
    val label: String,
) {
    REASON_1("자주 사용하지 않아요"),
    REASON_2("원하는 펍 정보가 부족해요"),
    REASON_3("정보가 정확하지 않아요"),
    REASON_4("앱 오류·불편이 있어요"),
    ETC("기타 (직접 입력)"),
}
