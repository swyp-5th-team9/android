package org.app.presentation.mypage.withdraw

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.AuthRepository
import org.app.data.repository.api.UserRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository,
    ) : BaseViewModel<WithdrawContract.State, WithdrawContract.Event, WithdrawContract.SideEffect>(
            WithdrawContract.State(),
        ) {
        override fun onEvent(event: WithdrawContract.Event) {
            when (event) {
                is WithdrawContract.Event.OnReasonSelected -> selectReason(event.reason)
                is WithdrawContract.Event.OnEtcTextChanged -> onEtcTextChange(event.text)
                WithdrawContract.Event.OnAgreementToggle -> toggleAgreement()
                WithdrawContract.Event.OnWithdrawClick -> withdraw()
                WithdrawContract.Event.OnWithdrawConfirm -> onWithdrawConfirm()
            }
        }

        private fun selectReason(reason: WithdrawReason) {
            setState { copy(selectedReason = reason) }
        }

        private fun onEtcTextChange(text: String) {
            val error = if (text.isBlank()) "내용을 입력해 주세요." else null
            setState { copy(etcText = text, etcError = error) }
        }

        private fun toggleAgreement() {
            setState { copy(isAgreed = !isAgreed) }
        }

        private fun withdraw() {
            if (currentState.isLoading) return
            val reason = currentState.selectedReason ?: return
            if (reason == WithdrawReason.ETC && currentState.etcText.isBlank()) {
                setState { copy(etcError = "내용을 입력해 주세요.") }
                return
            }
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                val reasonCode = reason.toApiCode()
                val detail = if (reason == WithdrawReason.ETC) currentState.etcText.trim() else null

                userRepository
                    .deleteUser(reasonCode = reasonCode, detail = detail)
                    .onSuccess {
                        // 소셜 플랫폼 탈퇴 처리 (로컬 토큰 제거 포함)
                        authRepository.withdraw()
                        setState { copy(isLoading = false) }
                        postSideEffect(WithdrawContract.SideEffect.ShowSuccessDialog)
                        Timber.d("회원 탈퇴 성공")
                    }.onFailure { error ->
                        setState { copy(isLoading = false) }
                        postSideEffect(WithdrawContract.SideEffect.ShowToast("탈퇴에 실패했습니다. 잠시 후 다시 시도해주세요."))
                        Timber.e("회원 탈퇴 실패: $error")
                    }
            }
        }

        private fun onWithdrawConfirm() {
            postSideEffect(WithdrawContract.SideEffect.NavigateToLogin)
        }
    }

private fun WithdrawReason.toApiCode(): String =
    when (this) {
        WithdrawReason.REASON_1 -> "NO_USE"
        WithdrawReason.REASON_2 -> "INSUFFICIENT_DATA"
        WithdrawReason.REASON_3 -> "INACCURATE_DATA"
        WithdrawReason.REASON_4 -> "APP_ISSUE"
        WithdrawReason.ETC -> "OTHER"
    }
