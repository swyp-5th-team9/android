package org.app.presentation.mypage.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.ReportRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val reportRepository: ReportRepository,
    ) : BaseViewModel<ReportContract.State, ReportContract.Event, ReportContract.SideEffect>(
            ReportContract.State(pubId = savedStateHandle.get<Long?>("pubId")),
        ) {
        override fun onEvent(event: ReportContract.Event) {
            when (event) {
                is ReportContract.Event.OnCategorySelected ->
                    setState { copy(selectedCategory = event.category) }

                is ReportContract.Event.OnDetailTextChanged ->
                    setState {
                        val error = if (event.text.length > ReportContract.State.MAX_CONTENT_LENGTH) {
                            "최대 ${ReportContract.State.MAX_CONTENT_LENGTH}자까지 입력 가능합니다."
                        } else {
                            null
                        }
                        copy(detailText = event.text, detailError = error)
                    }

                is ReportContract.Event.OnImagesAdded ->
                    setState {
                        val merged = (imageUris + event.uris)
                            .distinct()
                            .take(ReportContract.State.MAX_IMAGES)
                        copy(imageUris = merged.toImmutableList())
                    }

                is ReportContract.Event.OnImageRemoved ->
                    setState { copy(imageUris = (imageUris - event.uri).toImmutableList()) }

                ReportContract.Event.OnSubmit -> submit()

                ReportContract.Event.OnBack -> postSideEffect(ReportContract.SideEffect.NavigateBack)
            }
        }

        private fun submit() {
            val s = currentState
            if (!s.canSubmit) return

            viewModelScope.launch {
                setState { copy(isSubmitting = true) }
                reportRepository
                    .postReport(
                        category = s.selectedCategory.apiValue,
                        content = s.detailText,
                        pubId = s.pubId,
                        imageUris = s.imageUris,
                    ).onSuccess {
                        Timber.d("제보 완료: reportId=${it.reportId}")
                        postSideEffect(ReportContract.SideEffect.ShowSuccessDialog)
                    }.onFailure { error ->
                        Timber.e(error, "제보 전송 실패")
                        postSideEffect(ReportContract.SideEffect.ShowToast("제보 전송에 실패했습니다. 잠시 후 다시 시도해 주세요."))
                    }
                setState { copy(isSubmitting = false) }
            }
        }
    }
