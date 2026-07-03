package org.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.data.model.PubMapItem
import org.app.data.repository.api.FavoriteRepository
import org.app.data.repository.api.PubRepository
import org.app.data.repository.api.UserRepository
import org.app.presentation.home.model.PubMarker
import org.app.presentation.home.model.PubMarkerType
import timber.log.Timber
import javax.inject.Inject

// 서울 전체를 커버하는 기본 BBox
private const val DEFAULT_SW_LAT = 37.413294
private const val DEFAULT_SW_LNG = 126.734086
private const val DEFAULT_NE_LAT = 37.715133
private const val DEFAULT_NE_LNG = 127.269311

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val pubRepository: PubRepository,
        private val favoriteRepository: FavoriteRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(HomeContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<HomeContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        // 현재 지도 BBox (OnMapBoundsChanged 로 업데이트)
        private var currentSwLat = DEFAULT_SW_LAT
        private var currentSwLng = DEFAULT_SW_LNG
        private var currentNeLat = DEFAULT_NE_LAT
        private var currentNeLng = DEFAULT_NE_LNG

        init {
            loadUserFavoriteTeams()
            loadMapPubs()
            loadFavoritePubIds()
        }

        fun refreshFavoriteTeams() = loadUserFavoriteTeams()

        private fun loadUserFavoriteTeams() {
            viewModelScope.launch {
                userRepository
                    .getUser()
                    .onSuccess { user ->
                        _state.update {
                            it.copy(
                                userFavoriteTeamIds = user.favoriteTeams.map { t -> t.teamId },
                                userFavoriteTeamNames = user.favoriteTeams.map { t -> t.teamName },
                            )
                        }
                    }.onFailure { Timber.e("응원 구단 로드 실패: $it") }
            }
        }

        private fun loadFavoritePubIds() {
            viewModelScope.launch {
                favoriteRepository
                    .getFavorites()
                    .onSuccess { items ->
                        _state.update { it.copy(favoritePubIds = items.map { f -> f.pubId }.toSet()) }
                    }.onFailure { Timber.e("찜 목록 로드 실패: $it") }
            }
        }

        private fun loadMapPubs(
            teamId: Long? = null,
            openNow: Boolean? = null,
            businessDay: String? = null,
            showEmptyToast: Boolean = false,
        ) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                pubRepository
                    .getMapPubs(
                        swLat = currentSwLat,
                        swLng = currentSwLng,
                        neLat = currentNeLat,
                        neLng = currentNeLng,
                        teamId = teamId,
                        openNow = openNow,
                        businessDay = businessDay,
                    ).onSuccess { items ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                pubMapItems = items,
                                pubMarkers = items.toMarkers(),
                                pubClusters = emptyList(),
                                showPubListSheet = items.isNotEmpty() && !it.showPubDetailSheet,
                            )
                        }
                        if (showEmptyToast && items.isEmpty()) {
                            emit(HomeContract.SideEffect.ShowToast("해당 조건에 맞는 펍이 없습니다."))
                        }
                    }.onFailure { error ->
                        Timber.e("지도 펍 로드 실패: $error")
                        _state.update { it.copy(isLoading = false) }
                    }
            }
        }

        private fun loadSelectedPubDetail(pubId: Long) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isPubDetailLoading = true,
                        showPubDetailSheet = true,
                        showPubListSheet = false,
                    )
                }

                val detailResult = pubRepository.getPubDetail(pubId)
                val favoriteResult = favoriteRepository.getFavorites()

                detailResult
                    .onSuccess { detail ->
                        val favoriteItem = favoriteResult.getOrNull()?.find { it.pubId == pubId }
                        _state.update {
                            it.copy(
                                isPubDetailLoading = false,
                                selectedPubDetail = detail.copy(isWishlisted = favoriteItem != null),
                                selectedPubFavoriteId = favoriteItem?.favoriteId,
                            )
                        }
                    }.onFailure { error ->
                        Timber.e("펍 상세 로드 실패: $error")
                        _state.update {
                            it.copy(
                                isPubDetailLoading = false,
                                showPubDetailSheet = false,
                                showPubListSheet = it.pubMapItems.isNotEmpty(),
                            )
                        }
                        emit(HomeContract.SideEffect.ShowToast("펍 정보를 불러오지 못했습니다."))
                    }
            }
        }

        private fun List<PubMapItem>.toMarkers(): List<PubMarker> =
            map { item ->
                PubMarker(
                    pubId = item.pubId.toString(),
                    name = item.name,
                    latitude = item.latitude,
                    longitude = item.longitude,
                    type = PubMarkerType.MATCH,
                    isFavorite = false,
                )
            }

        fun onEvent(event: HomeContract.Event) {
            when (event) {
                HomeContract.Event.OnSearchBarClick ->
                    emit(HomeContract.SideEffect.NavigateToSearch)

                HomeContract.Event.OnMenuFilterClick ->
                    emit(HomeContract.SideEffect.NavigateToPubFilter)

                HomeContract.Event.OnTeamChipClick ->
                    _state.update {
                        it.copy(showFilterBottomSheet = true, filterBottomSheetTab = FilterBottomSheetTab.TEAM)
                    }

                HomeContract.Event.OnRegionChipClick ->
                    _state.update {
                        it.copy(showFilterBottomSheet = true, filterBottomSheetTab = FilterBottomSheetTab.REGION)
                    }

                HomeContract.Event.OnFilterBottomSheetDismiss ->
                    _state.update { it.copy(showFilterBottomSheet = false) }

                HomeContract.Event.OnReportClick ->
                    emit(HomeContract.SideEffect.NavigateToReport)

                HomeContract.Event.OnMyLocationClick -> {
                    // NaverMap 위치 추적은 Screen 레이어에서 직접 처리
                }

                HomeContract.Event.OnRegionSearchClick -> {
                    val filter = _state.value.filter
                    loadMapPubs(
                        teamId = filter.selectedTeamIds.firstOrNull(),
                        openNow = filter.openNow,
                        businessDay = filter.businessDay,
                        showEmptyToast = true,
                    )
                }

                HomeContract.Event.OnPubDetailSheetDismiss ->
                    _state.update {
                        it.copy(
                            showPubDetailSheet = false,
                            selectedPubDetail = null,
                            selectedPubFavoriteId = null,
                            isPubFavoriteLoading = false,
                            showPubListSheet = it.pubMapItems.isNotEmpty(),
                        )
                    }

                HomeContract.Event.OnPubListSheetDismiss ->
                    _state.update { it.copy(showPubListSheet = false) }

                HomeContract.Event.OnFavoriteClick -> {
                    if (_state.value.isPubFavoriteLoading) return
                    val detail = _state.value.selectedPubDetail ?: return
                    if (detail.isWishlisted) {
                        emit(HomeContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
                        return
                    }
                    viewModelScope.launch {
                        _state.update { it.copy(isPubFavoriteLoading = true) }
                        favoriteRepository
                            .addFavorite(detail.pubId)
                            .onSuccess { newId ->
                                _state.update {
                                    it.copy(
                                        isPubFavoriteLoading = false,
                                        selectedPubFavoriteId = newId,
                                        selectedPubDetail = detail.copy(
                                            isWishlisted = true,
                                            favoriteCount = detail.favoriteCount + 1,
                                        ),
                                        favoritePubIds = it.favoritePubIds + detail.pubId,
                                    )
                                }
                                emit(HomeContract.SideEffect.ShowToast("즐겨찾기에 등록되었습니다."))
                            }.onFailure {
                                _state.update { s -> s.copy(isPubFavoriteLoading = false) }
                                emit(HomeContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
                            }
                    }
                }

                is HomeContract.Event.OnMapBoundsChanged -> {
                    currentSwLat = event.swLat
                    currentSwLng = event.swLng
                    currentNeLat = event.neLat
                    currentNeLng = event.neLng
                }

                is HomeContract.Event.OnPubMarkerClick ->
                    loadSelectedPubDetail(event.pubId.toLongOrNull() ?: return)

                is HomeContract.Event.OnPubListItemClick ->
                    loadSelectedPubDetail(event.pubId)

                is HomeContract.Event.OnFilterApply -> {
                    val newFilter = _state.value.filter.copy(
                        selectedTeamIds = event.teamIds,
                        selectedTeamNames = event.teamNames,
                        selectedRegions = event.regions,
                        openNow = event.openNow,
                        businessDay = event.businessDay,
                    )
                    _state.update {
                        it.copy(
                            filter = newFilter,
                            showFilterBottomSheet = false,
                        )
                    }

                    // 선택된 지역들의 좌표를 모아 바운즈 이동 처리
                    val points = event.regions.mapNotNull {
                        org.app.presentation.home.model.RegionMapper
                            .getLatLng(it)
                    }
                    if (points.isNotEmpty()) {
                        emit(HomeContract.SideEffect.MoveCameraToBounds(points))
                    }

                    loadMapPubs(
                        teamId = event.teamIds.firstOrNull(),
                        openNow = event.openNow,
                        businessDay = event.businessDay,
                        showEmptyToast = true,
                    )
                }

                is HomeContract.Event.OnKakaoMapClick -> {
                    val kakaoFallback =
                        "https://map.kakao.com/link/map/${event.name},${event.lat},${event.lng}"
                    emit(
                        HomeContract.SideEffect.OpenMap(
                            url = "kakaomap://look?p=${event.lat},${event.lng}",
                            appScheme = "kakaomap",
                            webFallbackUrl = kakaoFallback,
                        ),
                    )
                }

                is HomeContract.Event.OnNaverMapClick -> {
                    val naverUrl =
                        "nmap://place?lat=${event.lat}&lng=${event.lng}" +
                            "&name=${event.name}&appname=org.app"
                    emit(
                        HomeContract.SideEffect.OpenMap(
                            url = naverUrl,
                            appScheme = "nmap",
                            webFallbackUrl = "https://map.naver.com/v5/search/${event.name}",
                        ),
                    )
                }

                is HomeContract.Event.OnQuickFilterClick -> {
                    val currentFilter = _state.value.filter
                    val newFilter = when (event.filterKey) {
                        "OPEN" -> currentFilter.copy(openNow = if (currentFilter.openNow == true) null else true)
                        // TODO: 다른 필터 키 처리 (데이터 모델 확장에 따라)
                        else -> currentFilter
                    }

                    _state.update { it.copy(filter = newFilter) }
                    loadMapPubs(
                        teamId = newFilter.selectedTeamIds.firstOrNull(),
                        openNow = newFilter.openNow,
                        businessDay = newFilter.businessDay,
                        showEmptyToast = true,
                    )
                }
            }
        }

        private fun emit(effect: HomeContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
