package org.app.presentation.pubdetail

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
import org.app.data.repository.api.PubRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PubDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val pubRepository: PubRepository,
    ) : ViewModel() {
        private val pubId: Long =
            savedStateHandle.get<String>("pubId")?.toLongOrNull() ?: 0L

        private val _state = MutableStateFlow(PubDetailContract.State(pubId = pubId.toString()))
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<PubDetailContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadPubDetail()
        }

        fun onEvent(event: PubDetailContract.Event) {
            when (event) {
                is PubDetailContract.Event.OnBack ->
                    emit(PubDetailContract.SideEffect.NavigateBack)

                is PubDetailContract.Event.OnWishlistToggle -> {
                    // TODO: FavoriteRepository 연결 (PR #46 머지 후)
                    val detail = _state.value.pubDetail ?: return
                    _state.update { it.copy(pubDetail = detail.copy(isWishlisted = !detail.isWishlisted)) }
                }

                is PubDetailContract.Event.OnImagePageChanged ->
                    _state.update { it.copy(currentImageIndex = event.index) }

                is PubDetailContract.Event.OnPhoneCall -> {
                    val phone = _state.value.pubDetail?.phoneNumber ?: return
                    emit(PubDetailContract.SideEffect.CallPhone(phone))
                }

                is PubDetailContract.Event.OnKakaoMapClick -> {
                    val detail = _state.value.pubDetail ?: return
                    val kakaoFallback =
                        "https://map.kakao.com/link/map/${detail.name}," +
                            "${detail.latitude},${detail.longitude}"
                    emit(
                        PubDetailContract.SideEffect.OpenMap(
                            url = "kakaomap://look?p=${detail.latitude},${detail.longitude}",
                            appScheme = "kakaomap",
                            webFallbackUrl = kakaoFallback,
                        ),
                    )
                }

                is PubDetailContract.Event.OnNaverMapClick -> {
                    val detail = _state.value.pubDetail ?: return
                    val naverUrl =
                        "nmap://place?lat=${detail.latitude}&lng=${detail.longitude}" +
                            "&name=${detail.name}&appname=org.app"
                    emit(
                        PubDetailContract.SideEffect.OpenMap(
                            url = naverUrl,
                            appScheme = "nmap",
                            webFallbackUrl = "https://map.naver.com/v5/search/${detail.name}",
                        ),
                    )
                }

                is PubDetailContract.Event.OnHoursToggle ->
                    _state.update { it.copy(isHoursExpanded = !it.isHoursExpanded) }

                is PubDetailContract.Event.OnPhotoClick ->
                    _state.update { it.copy(showPhotoGallery = true, selectedPhotoIndex = event.index) }

                is PubDetailContract.Event.OnPhotoGalleryClose ->
                    _state.update { it.copy(showPhotoGallery = false) }

                is PubDetailContract.Event.OnPhotoGalleryPageChanged ->
                    _state.update { it.copy(selectedPhotoIndex = event.index) }
            }
        }

        private fun loadPubDetail() {
            if (pubId == 0L) return
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                pubRepository
                    .getPubDetail(pubId)
                    .onSuccess { detail ->
                        _state.update { it.copy(isLoading = false, pubDetail = detail) }
                    }.onFailure { error ->
                        Timber.e("펍 상세 로드 실패: $error")
                        _state.update { it.copy(isLoading = false) }
                        emit(PubDetailContract.SideEffect.ShowToast("펍 정보를 불러오지 못했습니다."))
                    }
            }
        }

        private fun emit(effect: PubDetailContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
