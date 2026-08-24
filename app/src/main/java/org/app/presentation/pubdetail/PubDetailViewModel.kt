package org.app.presentation.pubdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.core.network.isHttpConflict
import org.app.data.repository.api.FavoriteRepository
import org.app.data.repository.api.PubRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PubDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val pubRepository: PubRepository,
        private val favoriteRepository: FavoriteRepository,
    ) : BaseViewModel<PubDetailContract.State, PubDetailContract.Event, PubDetailContract.SideEffect>(
            PubDetailContract.State(
                pubId = (savedStateHandle.get<String>("pubId")?.toLongOrNull() ?: 0L).toString(),
            ),
        ) {
        private val pubId: Long =
            savedStateHandle.get<String>("pubId")?.toLongOrNull() ?: 0L

        init {
            loadPubDetail()
        }

        override fun onEvent(event: PubDetailContract.Event) {
            when (event) {
                is PubDetailContract.Event.OnBack ->
                    postSideEffect(PubDetailContract.SideEffect.NavigateBack)

                is PubDetailContract.Event.OnFavoriteToggle -> toggleFavorite()

                is PubDetailContract.Event.OnImagePageChanged ->
                    setState { copy(currentImageIndex = event.index) }

                is PubDetailContract.Event.OnPhoneCall -> {
                    val phone = currentState.pubDetail?.phoneNumber ?: return
                    postSideEffect(PubDetailContract.SideEffect.CallPhone(phone))
                }

                is PubDetailContract.Event.OnKakaoMapClick -> {
                    val detail = currentState.pubDetail ?: return
                    val kakaoFallback =
                        "https://map.kakao.com/link/map/${detail.name}," +
                            "${detail.latitude},${detail.longitude}"
                    postSideEffect(
                        PubDetailContract.SideEffect.OpenMap(
                            url = "kakaomap://look?p=${detail.latitude},${detail.longitude}",
                            appScheme = "kakaomap",
                            webFallbackUrl = kakaoFallback,
                        ),
                    )
                }

                is PubDetailContract.Event.OnNaverMapClick -> {
                    val detail = currentState.pubDetail ?: return
                    val naverUrl =
                        "nmap://place?lat=${detail.latitude}&lng=${detail.longitude}" +
                            "&name=${detail.name}&appname=org.app"
                    postSideEffect(
                        PubDetailContract.SideEffect.OpenMap(
                            url = naverUrl,
                            appScheme = "nmap",
                            webFallbackUrl = "https://map.naver.com/v5/search/${detail.name}",
                        ),
                    )
                }

                is PubDetailContract.Event.OnHoursToggle ->
                    setState { copy(isHoursExpanded = !isHoursExpanded) }

                is PubDetailContract.Event.OnPhotoClick ->
                    setState { copy(showPhotoGallery = true, selectedPhotoIndex = event.index) }

                is PubDetailContract.Event.OnPhotoGalleryClose ->
                    setState { copy(showPhotoGallery = false) }

                is PubDetailContract.Event.OnPhotoGalleryPageChanged ->
                    setState { copy(selectedPhotoIndex = event.index) }

                is PubDetailContract.Event.OnShareClick ->
                    setState { copy(showShareSheet = true) }

                is PubDetailContract.Event.OnShareSheetDismiss ->
                    setState { copy(showShareSheet = false) }

                is PubDetailContract.Event.OnCopyLink -> {
                    val detail = currentState.pubDetail ?: return
                    // 시트는 열어둔 채 "복사됨" 상태를 노출한다(디자인).
                    postSideEffect(
                        PubDetailContract.SideEffect.CopyLinkToClipboard(PubShareLink.url(detail.pubId)),
                    )
                }

                is PubDetailContract.Event.OnShareToOtherApps -> {
                    val detail = currentState.pubDetail ?: return
                    setState { copy(showShareSheet = false) }
                    postSideEffect(
                        PubDetailContract.SideEffect.ShareLinkToOtherApps(
                            PubShareLink.shareText(pubName = detail.name, pubId = detail.pubId),
                        ),
                    )
                }
            }
        }

        private fun toggleFavorite() {
            if (currentState.isFavoriteLoading) return
            val detail = currentState.pubDetail ?: return

            viewModelScope.launch {
                setState { copy(isFavoriteLoading = true) }
                if (detail.isFavoriteed) {
                    removeFavorite()
                } else {
                    addFavorite()
                }
            }
        }

        private suspend fun addFavorite() {
            val detail = currentState.pubDetail ?: return
            favoriteRepository
                .addFavorite(pubId)
                .onSuccess { newId ->
                    setState {
                        copy(
                            isFavoriteLoading = false,
                            favoriteId = newId,
                            pubDetail = detail.copy(
                                isFavoriteed = true,
                                favoriteCount = detail.favoriteCount + 1,
                            ),
                        )
                    }
                    postSideEffect(PubDetailContract.SideEffect.ShowToast("즐겨찾기에 등록되었습니다."))
                }.onFailure { error ->
                    setState { copy(isFavoriteLoading = false) }
                    if (error.isHttpConflict()) {
                        loadPubDetail() // 이미 등록된 경우 상태 동기화
                        postSideEffect(PubDetailContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
                    } else {
                        postSideEffect(PubDetailContract.SideEffect.ShowToast("즐겨찾기 등록에 실패했습니다."))
                    }
                }
        }

        private suspend fun removeFavorite() {
            // ID가 없는 경우(홈에서 진입 등) 전체 목록에서 조회한다.
            val favoriteId = currentState.favoriteId
                ?: favoriteRepository
                    .getFavorites()
                    .getOrNull()
                    ?.find { it.pubId == pubId }
                    ?.favoriteId

            if (favoriteId == null) {
                setState { copy(isFavoriteLoading = false) }
                loadPubDetail() // 상태 강제 동기화
                return
            }

            val detail = currentState.pubDetail ?: return
            favoriteRepository
                .deleteFavorites(listOf(favoriteId))
                .onSuccess {
                    setState {
                        copy(
                            isFavoriteLoading = false,
                            favoriteId = null,
                            pubDetail = detail.copy(
                                isFavoriteed = false,
                                favoriteCount = (detail.favoriteCount - 1).coerceAtLeast(0),
                            ),
                        )
                    }
                    postSideEffect(PubDetailContract.SideEffect.ShowToast("즐겨찾기에서 해제되었습니다."))
                }.onFailure {
                    setState { copy(isFavoriteLoading = false) }
                    postSideEffect(PubDetailContract.SideEffect.ShowToast("즐겨찾기 해제에 실패했습니다."))
                }
        }

        private fun loadPubDetail() {
            if (pubId == 0L) return
            viewModelScope.launch {
                setState { copy(isLoading = true) }

                // 서로 독립적인 요청이므로 병렬 실행
                val detailDeferred = async { pubRepository.getPubDetail(pubId) }
                val favoriteDeferred = async { favoriteRepository.getFavorites() }
                val detailResult = detailDeferred.await()
                val favoriteResult = favoriteDeferred.await()

                detailResult
                    .onSuccess { detail ->
                        val favoriteItem = favoriteResult.getOrNull()?.find { it.pubId == pubId }
                        setState {
                            copy(
                                isLoading = false,
                                pubDetail = detail.copy(isFavoriteed = favoriteItem != null),
                                favoriteId = favoriteItem?.favoriteId,
                            )
                        }
                    }.onFailure { error ->
                        Timber.e("펍 상세 로드 실패: $error")
                        setState { copy(isLoading = false) }
                        postSideEffect(PubDetailContract.SideEffect.ShowToast("펍 정보를 불러오지 못했습니다."))
                    }
            }
        }
    }
