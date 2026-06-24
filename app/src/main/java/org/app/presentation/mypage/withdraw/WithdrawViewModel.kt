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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(WithdrawContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<WithdrawContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun selectReason(reason: WithdrawReason) {
            _state.update { it.copy(selectedReason = reason) }
        }

        fun onEtcTextChange(text: String) {
            _state.update { it.copy(etcText = text) }
        }

        fun toggleAgreement() {
            _state.update { it.copy(isAgreed = !it.isAgreed) }
        }

        fun withdraw() {
            viewModelScope.launch {
                authRepository
                    .withdraw()
                    .onSuccess {
                        _sideEffect.emit(WithdrawContract.SideEffect.ShowToast("회원 탈퇴가 완료됐어요."))
                        _sideEffect.emit(WithdrawContract.SideEffect.NavigateToLogin)
                        Timber.d("회원 탈퇴 성공")
                    }.onFailure { error ->
                        _sideEffect.emit(WithdrawContract.SideEffect.ShowToast("탈퇴 실패: ${error.message}"))
                        Timber.e("회원 탈퇴 실패: $error")
                    }
            }
        }
    }
