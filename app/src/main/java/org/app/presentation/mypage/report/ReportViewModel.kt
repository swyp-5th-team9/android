package org.app.presentation.mypage.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(ReportContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<ReportContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun selectCategory(category: ReportCategory) {
            _state.update { it.copy(selectedCategory = category) }
        }

        fun onDetailTextChange(text: String) {
            _state.update { it.copy(detailText = text) }
        }

        fun addScreenshots(uris: List<String>) {
            _state.update {
                val newList = (it.screenshots + uris).take(3)
                it.copy(screenshots = newList)
            }
        }

        fun removeScreenshot(uri: String) {
            _state.update {
                it.copy(screenshots = it.screenshots - uri)
            }
        }

        fun submit() {
            viewModelScope.launch {
                _state.update { it.copy(isSubmitting = true) }
                try {
                    // TODO: API 연결
                    Timber.d("제보 전송: category=${_state.value.selectedCategory}, detail=${_state.value.detailText}")
                    _sideEffect.emit(ReportContract.SideEffect.ShowSuccessDialog)
                } catch (e: Exception) {
                    _sideEffect.emit(ReportContract.SideEffect.ShowToast("제보 전송 실패: ${e.message}"))
                    Timber.e("제보 전송 실패: $e")
                } finally {
                    _state.update { it.copy(isSubmitting = false) }
                }
            }
        }
    }
