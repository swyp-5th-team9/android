package org.app.presentation.mypage.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    ) : ViewModel() {
        private val _state = MutableStateFlow(WithdrawContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<WithdrawContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun onEvent(event: WithdrawContract.Event) {
            when (event) {
                is WithdrawContract.Event.OnReasonSelected -> selectReason(event.reason)
                is WithdrawContract.Event.OnEtcTextChanged -> onEtcTextChange(event.text)
                WithdrawContract.Event.OnAgreementToggle -> toggleAgreement()
                WithdrawContract.Event.OnWithdrawClick -> withdraw()
                WithdrawContract.Event.OnWithdrawConfirm -> onWithdrawConfirm()
            }
        }

        private fun selectReason(reason: WithdrawReason) {
            _state.update { it.copy(selectedReason = reason) }
        }

        private fun onEtcTextChange(text: String) {
            _state.update { it.copy(etcText = text) }
        }

        private fun toggleAgreement() {
            _state.update { it.copy(isAgreed = !it.isAgreed) }
        }

        private fun withdraw() {
            if (_state.value.isLoading) return
            val reason = _state.value.selectedReason ?: return
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val reasonCode = reason.toApiCode()
                val detail = if (reason == WithdrawReason.ETC) _state.value.etcText.trim() else null

                userRepository
                    .deleteUser(reasonCode = reasonCode, detail = detail)
                    .onSuccess {
                        // 소셜 플랫폼 탈퇴 처리 (로컬 토큰 제거 포함)
                        authRepository.withdraw()
                        _state.update { it.copy(isLoading = false) }
                        _sideEffect.emit(WithdrawContract.SideEffect.ShowSuccessDialog)
                        Timber.d("회원 탈퇴 성공")
                    }.onFailure { error ->
                        _state.update { it.copy(isLoading = false) }
                        _sideEffect.emit(WithdrawContract.SideEffect.ShowToast("탈퇴 실패: ${error.message}"))
                        Timber.e("회원 탈퇴 실패: $error")
                    }
            }
        }

        private fun onWithdrawConfirm() {
            viewModelScope.launch {
                _sideEffect.emit(WithdrawContract.SideEffect.NavigateToLogin)
            }
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
