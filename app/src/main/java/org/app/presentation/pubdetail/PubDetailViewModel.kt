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
import org.app.presentation.pubdetail.model.BusinessHours
import org.app.presentation.pubdetail.model.BusinessStatus
import org.app.presentation.pubdetail.model.FacilityItem
import org.app.presentation.pubdetail.model.FacilityType
import org.app.presentation.pubdetail.model.KboTeam
import org.app.presentation.pubdetail.model.ParkingItem
import org.app.presentation.pubdetail.model.ParkingType
import org.app.presentation.pubdetail.model.PubDetail
import javax.inject.Inject

@HiltViewModel
class PubDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val favoriteRepository: FavoriteRepository,
    ) : ViewModel() {
        private val pubId: String = savedStateHandle.get<String>("pubId") ?: ""

        private val _state = MutableStateFlow(PubDetailContract.State(pubId = pubId))
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<PubDetailContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadMockData()
        }

        fun onEvent(event: PubDetailContract.Event) {
            when (event) {
                is PubDetailContract.Event.OnBack -> {
                    viewModelScope.launch {
                        _sideEffect.emit(PubDetailContract.SideEffect.NavigateBack)
                    }
                }

                is PubDetailContract.Event.OnWishlistToggle -> {
                    val detail = _state.value.pubDetail ?: return
                    if (_state.value.isWishlistLoading) return
                    viewModelScope.launch {
                        _state.update { it.copy(isWishlistLoading = true) }
                        if (detail.isWishlisted) {
                            // 찜 해제
                            val favId = _state.value.favoriteId
                            if (favId == null) {
                                _state.update { it.copy(isWishlistLoading = false) }
                                _sideEffect.emit(PubDetailContract.SideEffect.ShowToast("오류가 발생했습니다."))
                                return@launch
                            }
                            favoriteRepository
                                .deleteFavorites(listOf(favId))
                                .onSuccess {
                                    _state.update { s ->
                                        s.copy(
                                            isWishlistLoading = false,
                                            favoriteId = null,
                                            pubDetail = detail.copy(
                                                isWishlisted = false,
                                                wishlistCount = (detail.wishlistCount - 1).coerceAtLeast(0),
                                            ),
                                        )
                                    }
                                }.onFailure { e ->
                                    _state.update { it.copy(isWishlistLoading = false) }
                                    _sideEffect.emit(
                                        PubDetailContract.SideEffect.ShowToast(e.message ?: "찜 해제에 실패했습니다."),
                                    )
                                }
                        } else {
                            val pubIdLong = pubId.toLongOrNull() ?: run {
                                _state.update { it.copy(isWishlistLoading = false) }
                                _sideEffect.emit(PubDetailContract.SideEffect.ShowToast("오류가 발생했습니다."))
                                return@launch
                            }
                            favoriteRepository
                                .addFavorite(pubIdLong)
                                .onSuccess { newFavoriteId ->
                                    _state.update { s ->
                                        s.copy(
                                            isWishlistLoading = false,
                                            favoriteId = newFavoriteId,
                                            pubDetail = detail.copy(
                                                isWishlisted = true,
                                                wishlistCount = detail.wishlistCount + 1,
                                            ),
                                        )
                                    }
                                }.onFailure { e ->
                                    _state.update { it.copy(isWishlistLoading = false) }
                                    _sideEffect.emit(
                                        PubDetailContract.SideEffect.ShowToast(e.message ?: "찜 추가에 실패했습니다."),
                                    )
                                }
                        }
                    }
                }

                is PubDetailContract.Event.OnImagePageChanged -> {
                    _state.update { it.copy(currentImageIndex = event.index) }
                }

                is PubDetailContract.Event.OnPhoneCall -> {
                    val phone = _state.value.pubDetail?.phoneNumber ?: return
                    viewModelScope.launch {
                        _sideEffect.emit(PubDetailContract.SideEffect.CallPhone(phone))
                    }
                }

                is PubDetailContract.Event.OnKakaoMapClick -> {
                    val detail = _state.value.pubDetail ?: return
                    val lat = detail.latitude ?: return
                    val lng = detail.longitude ?: return
                    val name = detail.name
                    viewModelScope.launch {
                        _sideEffect.emit(
                            PubDetailContract.SideEffect.OpenMap(
                                url = "kakaomap://look?p=$lat,$lng",
                                appScheme = "kakaomap",
                                webFallbackUrl = "https://map.kakao.com/link/map/$name,$lat,$lng",
                            ),
                        )
                    }
                }

                is PubDetailContract.Event.OnNaverMapClick -> {
                    val detail = _state.value.pubDetail ?: return
                    val lat = detail.latitude ?: return
                    val lng = detail.longitude ?: return
                    val name = detail.name
                    viewModelScope.launch {
                        _sideEffect.emit(
                            PubDetailContract.SideEffect.OpenMap(
                                url = "nmap://place?lat=$lat&lng=$lng&name=$name&appname=org.app",
                                appScheme = "nmap",
                                webFallbackUrl = "https://map.naver.com/v5/search/$name",
                            ),
                        )
                    }
                }

                is PubDetailContract.Event.OnHoursToggle -> {
                    _state.update { it.copy(isHoursExpanded = !it.isHoursExpanded) }
                }

                is PubDetailContract.Event.OnPhotoClick -> {
                    _state.update {
                        it.copy(
                            showPhotoGallery = true,
                            selectedPhotoIndex = event.index,
                        )
                    }
                }

                is PubDetailContract.Event.OnPhotoGalleryClose -> {
                    _state.update { it.copy(showPhotoGallery = false) }
                }

                is PubDetailContract.Event.OnPhotoGalleryPageChanged -> {
                    _state.update { it.copy(selectedPhotoIndex = event.index) }
                }
            }
        }

        private fun loadMockData() {
            // TODO: 서버 API 연동 시 pubId 기준으로 실제 데이터 로드
            _state.update {
                it.copy(
                    isLoading = false,
                    pubDetail = PubDetail(
                        id = pubId.ifEmpty { "mock-001" },
                        name = "시그니처 펍",
                        imageUrls = listOf(
                            "https://picsum.photos/seed/pub1/400/400",
                            "https://picsum.photos/seed/pub2/400/400",
                            "https://picsum.photos/seed/pub3/400/400",
                            "https://picsum.photos/seed/pub4/400/400",
                            "https://picsum.photos/seed/pub5/400/400",
                            "https://picsum.photos/seed/pub6/400/400",
                            "https://picsum.photos/seed/pub7/400/400",
                            "https://picsum.photos/seed/pub8/400/400",
                            "https://picsum.photos/seed/pub9/400/400",
                            "https://picsum.photos/seed/pub10/400/400",
                        ),
                        teams = listOf(
                            KboTeam(id = 3, shortName = "LG", fullName = "LG 트윈스"),
                            KboTeam(id = 6, shortName = "두산", fullName = "두산 베어스"),
                        ),
                        isWishlisted = false,
                        wishlistCount = 12,
                        address = "서울특별시 마포구 월드컵북로 396",
                        phoneNumber = "02-1234-5678",
                        businessHours = BusinessHours(
                            openTime = "17:00",
                            closeTime = "02:00",
                            lastOrder = "01:30",
                            isOpenNow = true,
                            status = BusinessStatus.OPEN,
                        ),
                        seatCount = 60,
                        facilities = listOf(
                            FacilityItem(FacilityType.GROUP_SEATING),
                            FacilityItem(FacilityType.PROJECTOR),
                            FacilityItem(FacilityType.SOUND_SYSTEM),
                            FacilityItem(FacilityType.PRIVATE_ROOM),
                        ),
                        parkingOptions = listOf(
                            ParkingItem(ParkingType.NEARBY, "도보 3분 내 공영주차장"),
                        ),
                        latitude = 37.5516,
                        longitude = 126.9086,
                    ),
                )
            }
        }
    }
