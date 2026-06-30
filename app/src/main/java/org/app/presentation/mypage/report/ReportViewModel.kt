package org.app.presentation.mypage.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.data.repository.api.ReportRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val reportRepository: ReportRepository,
    ) : ViewModel() {
        private val pubId: Long? = savedStateHandle.get<Long?>("pubId")

        private val _state = MutableStateFlow(ReportContract.State(pubId = pubId))
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<ReportContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun onEvent(event: ReportContract.Event) {
            when (event) {
                is ReportContract.Event.OnCategorySelected ->
                    _state.update { it.copy(selectedCategory = event.category) }

                is ReportContract.Event.OnDetailTextChanged -> {
                    _state.update { it.copy(detailText = event.text, detailError = null) }
                }

                is ReportContract.Event.OnImagesAdded -> {
                    _state.update { s ->
                        val merged = (s.imageUris + event.uris)
                            .distinct()
                            .take(ReportContract.State.MAX_IMAGES)
                        s.copy(imageUris = merged)
                    }
                }

                is ReportContract.Event.OnImageRemoved ->
                    _state.update { it.copy(imageUris = it.imageUris - event.uri) }

                ReportContract.Event.OnSubmit -> submit()

                ReportContract.Event.OnBack ->
                    emit(ReportContract.SideEffect.NavigateBack)
            }
        }

        private fun submit() {
            val s = _state.value
            if (!s.canSubmit) return

            viewModelScope.launch {
                _state.update { it.copy(isSubmitting = true) }
                reportRepository
                    .postReport(
                        category = s.selectedCategory.apiValue,
                        content = s.detailText,
                        pubId = s.pubId,
                        imageUris = s.imageUris,
                    ).onSuccess {
                        Timber.d("제보 완료: reportId=${it.reportId}")
                        emit(ReportContract.SideEffect.ShowSuccessDialog)
                    }.onFailure { error ->
                        Timber.e(error, "제보 전송 실패")
                        emit(ReportContract.SideEffect.ShowToast("제보 전송에 실패했습니다. 잠시 후 다시 시도해 주세요."))
                    }
                _state.update { it.copy(isSubmitting = false) }
            }
        }

        private fun emit(effect: ReportContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
