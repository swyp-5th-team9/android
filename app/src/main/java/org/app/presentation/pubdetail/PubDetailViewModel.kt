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
import org.app.data.repository.api.FavoriteRepository
import org.app.data.repository.api.PubRepository
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PubDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val pubRepository: PubRepository,
        private val favoriteRepository: FavoriteRepository,
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
                    if (_state.value.isWishlistLoading) return
                    val detail = _state.value.pubDetail ?: return
                    val wasWishlisted = detail.isWishlisted

                    viewModelScope.launch {
                        _state.update { it.copy(isWishlistLoading = true) }
                        if (wasWishlisted) {
                            val favoriteId = _state.value.favoriteId
                            if (favoriteId != null) {
                                favoriteRepository
                                    .deleteFavorites(listOf(favoriteId))
                                    .onSuccess {
                                        _state.update {
                                            it.copy(
                                                isWishlistLoading = false,
                                                favoriteId = null,
                                                pubDetail = detail.copy(
                                                    isWishlisted = false,
                                                    favoriteCount = (detail.favoriteCount - 1).coerceAtLeast(0),
                                                ),
                                            )
                                        }
                                        emit(PubDetailContract.SideEffect.ShowToast("즐겨찾기에서 해제되었습니다."))
                                    }.onFailure {
                                        _state.update { it.copy(isWishlistLoading = false) }
                                        emit(PubDetailContract.SideEffect.ShowToast("즐겨찾기 해제에 실패했습니다."))
                                    }
                            } else {
                                // ID가 없는 경우 전체 목록 재조회 후 삭제 시도
                                favoriteRepository
                                    .getFavorites()
                                    .onSuccess { items ->
                                        val newId = items.find { it.pubId == pubId }?.favoriteId
                                        if (newId != null) {
                                            favoriteRepository
                                                .deleteFavorites(listOf(newId))
                                                .onSuccess {
                                                    _state.update {
                                                        it.copy(
                                                            isWishlistLoading = false,
                                                            favoriteId = null,
                                                            pubDetail = detail.copy(
                                                                isWishlisted = false,
                                                                favoriteCount = (detail.favoriteCount - 1)
                                                                    .coerceAtLeast(
                                                                        0,
                                                                    ),
                                                            ),
                                                        )
                                                    }
                                                    emit(PubDetailContract.SideEffect.ShowToast("즐겨찾기에서 해제되었습니다."))
                                                }.onFailure {
                                                    _state.update { it.copy(isWishlistLoading = false) }
                                                    emit(PubDetailContract.SideEffect.ShowToast("즐겨찾기 해제에 실패했습니다."))
                                                }
                                        } else {
                                            _state.update { it.copy(isWishlistLoading = false) }
                                            loadPubDetail() // 상태 강제 동기화
                                        }
                                    }.onFailure {
                                        _state.update { it.copy(isWishlistLoading = false) }
                                    }
                            }
                        } else {
                            favoriteRepository
                                .addFavorite(pubId)
                                .onSuccess { newId ->
                                    _state.update {
                                        it.copy(
                                            isWishlistLoading = false,
                                            favoriteId = newId,
                                            pubDetail = detail.copy(
                                                isWishlisted = true,
                                                favoriteCount = detail.favoriteCount + 1,
                                            ),
                                        )
                                    }
                                    emit(PubDetailContract.SideEffect.ShowToast("즐겨찾기에 등록되었습니다."))
                                }.onFailure { error ->
                                    _state.update { it.copy(isWishlistLoading = false) }
                                    if (error is HttpException && error.code() == 409) {
                                        loadPubDetail() // 이미 등록된 경우 상태 동기화
                                        emit(PubDetailContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
                                    } else {
                                        emit(PubDetailContract.SideEffect.ShowToast("즐겨찾기 등록에 실패했습니다."))
                                    }
                                }
                        }
                    }
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

                val detailResult = pubRepository.getPubDetail(pubId)
                val favoriteResult = favoriteRepository.getFavorites()

                detailResult
                    .onSuccess { detail ->
                        val favoriteItem = favoriteResult.getOrNull()?.find { it.pubId == pubId }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                pubDetail = detail.copy(isWishlisted = favoriteItem != null),
                                favoriteId = favoriteItem?.favoriteId,
                            )
                        }
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
