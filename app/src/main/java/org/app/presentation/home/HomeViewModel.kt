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
import org.app.data.mapper.toPartialDetail
import org.app.data.model.PubMapItem
import org.app.data.repository.api.FavoriteRepository
import org.app.data.repository.api.PubRepository
import org.app.data.repository.api.UserRepository
import org.app.presentation.home.model.PubMarker
import org.app.presentation.home.model.PubMarkerType
import timber.log.Timber
import javax.inject.Inject

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
        private var currentZoom = 14.0 // 기본 줌 레벨

        init {
            loadUserFavoriteTeams()
            loadMapPubs()
            loadPubList()
            loadFavoritePubIds()
        }

        fun refreshFavoriteTeams() = loadUserFavoriteTeams()

        fun refreshFavorites() = loadFavoritePubIds()

        private fun loadPubList(
            teamId: Long? = null,
            openNow: Boolean? = null,
            businessDay: String? = null,
            facilityCodes: List<String>? = null,
            themeCodes: List<String>? = null,
            foodCodes: List<String>? = null,
        ) {
            viewModelScope.launch {
                pubRepository
                    .getPubs(
                        teamId = teamId,
                        openNow = openNow,
                        businessDay = businessDay,
                        facilityCodes = facilityCodes,
                        themeCodes = themeCodes,
                        foodCodes = foodCodes,
                        size = 50,
                    ).onSuccess { page ->
                        _state.update { it.copy(pubListItems = page.content) }
                    }.onFailure { Timber.e("펍 목록 로드 실패: $it") }
            }
        }

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
                        val idMap = items.associate { it.pubId to it.favoriteId }
                        _state.update {
                            it.copy(
                                favoritePubIds = idMap.keys,
                                favoriteIdMap = idMap,
                                pubMarkers = if (currentZoom >
                                    13.0
                                ) {
                                    it.pubMapItems.toMarkers(idMap.keys)
                                } else {
                                    emptyList()
                                },
                                pubClusters = if (currentZoom <= 13.0) it.pubMapItems.toClusters() else emptyList(),
                            )
                        }
                    }.onFailure { Timber.e("찜 목록 로드 실패: $it") }
            }
        }

        private fun loadMapPubs(
            teamId: Long? = null,
            openNow: Boolean? = null,
            businessDay: String? = null,
            facilityCodes: List<String>? = null,
            themeCodes: List<String>? = null,
            foodCodes: List<String>? = null,
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
                        facilityCodes = facilityCodes,
                        themeCodes = themeCodes,
                        foodCodes = foodCodes,
                    ).onSuccess { items ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                pubMapItems = items,
                                pubMarkers = if (currentZoom >
                                    13.0
                                ) {
                                    items.toMarkers(it.favoritePubIds)
                                } else {
                                    emptyList()
                                },
                                pubClusters = if (currentZoom <= 13.0) items.toClusters() else emptyList(),
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
                val existingItem = _state.value.pubMapItems.find { it.pubId == pubId }
                val partialDetail = existingItem?.toPartialDetail()

                _state.update {
                    it.copy(
                        isPubDetailLoading = true,
                        showPubDetailSheet = true,
                        showPubListSheet = false,
                        selectedPubDetail = partialDetail ?: it.selectedPubDetail,
                    )
                }

                val detailResult = pubRepository.getPubDetail(pubId)

                detailResult
                    .onSuccess { detail ->
                        val isWishlisted = _state.value.favoritePubIds.contains(pubId)
                        _state.update {
                            it.copy(
                                isPubDetailLoading = false,
                                selectedPubDetail = detail.copy(isWishlisted = isWishlisted),
                            )
                        }
                    }.onFailure { error ->
                        Timber.e("펍 상세 로드 실패: $error")
                        _state.update {
                            it.copy(
                                isPubDetailLoading = false,
                                // 이미 데이터가 있으면(partial) 시트를 닫지 않음
                                showPubDetailSheet = it.selectedPubDetail != null,
                                showPubListSheet = it.pubMapItems.isNotEmpty() && it.selectedPubDetail == null,
                            )
                        }
                        emit(HomeContract.SideEffect.ShowToast("펍 정보를 불러오지 못했습니다."))
                    }
            }
        }

        private fun List<PubMapItem>.toMarkers(favoriteIds: Set<Long>): List<PubMarker> =
            map { item ->
                val isFavorite = item.pubId in favoriteIds
                PubMarker(
                    pubId = item.pubId.toString(),
                    name = item.name,
                    latitude = item.latitude,
                    longitude = item.longitude,
                    type = if (isFavorite) PubMarkerType.FAVORITE else PubMarkerType.MATCH,
                    isFavorite = isFavorite,
                )
            }

        private fun List<PubMapItem>.toClusters(): List<org.app.presentation.home.model.PubCluster> {
            if (isEmpty()) return emptyList()

            // 1. 위경도 오차범위 내의 펍들을 하나로 묶음 (그리드 방식)
            val gridSize = 0.05 // 클러스터링 범위 (줌 레벨에 따라 조정 가능)

            return groupBy {
                (it.latitude / gridSize).toInt() to (it.longitude / gridSize).toInt()
            }.map { (_, group) ->
                org.app.presentation.home.model.PubCluster(
                    latitude = group.map { it.latitude }.average(),
                    longitude = group.map { it.longitude }.average(),
                    count = group.size,
                    items = group,
                )
            }
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
                        facilityCodes = filter.facilityCodes,
                        themeCodes = filter.themeCodes,
                        foodCodes = filter.foodCodes,
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
                        )
                    }

                HomeContract.Event.OnPubListSheetDismiss ->
                    _state.update { it.copy(showPubListSheet = false, selectedPubList = emptyList()) }

                HomeContract.Event.OnFavoriteClick -> {
                    if (_state.value.isPubFavoriteLoading) return
                    val detail = _state.value.selectedPubDetail ?: return
                    val pubId = detail.pubId
                    val isWishlisted = _state.value.favoritePubIds.contains(pubId)

                    viewModelScope.launch {
                        _state.update { it.copy(isPubFavoriteLoading = true) }
                        if (isWishlisted) {
                            val favoriteId = _state.value.favoriteIdMap[pubId]
                            if (favoriteId != null) {
                                favoriteRepository
                                    .deleteFavorites(listOf(favoriteId))
                                    .onSuccess {
                                        _state.update {
                                            it.copy(
                                                isPubFavoriteLoading = false,
                                                selectedPubDetail = detail.copy(
                                                    isWishlisted = false,
                                                    favoriteCount = (detail.favoriteCount - 1).coerceAtLeast(0),
                                                ),
                                                favoritePubIds = it.favoritePubIds - pubId,
                                                favoriteIdMap = it.favoriteIdMap - pubId,
                                                pubMapItems = it.pubMapItems.map { item ->
                                                    if (item.pubId == pubId) {
                                                        item.copy(
                                                            favoriteCount = (item.favoriteCount - 1).coerceAtLeast(0),
                                                        )
                                                    } else {
                                                        item
                                                    }
                                                },
                                                pubMarkers = if (currentZoom > 13.0) {
                                                    it.pubMapItems
                                                        .map { item ->
                                                            if (item.pubId == pubId) {
                                                                item.copy(
                                                                    favoriteCount = (item.favoriteCount - 1)
                                                                        .coerceAtLeast(
                                                                            0,
                                                                        ),
                                                                )
                                                            } else {
                                                                item
                                                            }
                                                        }.toMarkers(it.favoritePubIds - pubId)
                                                } else {
                                                    emptyList()
                                                },
                                            )
                                        }
                                        emit(HomeContract.SideEffect.ShowToast("즐겨찾기에서 해제되었습니다."))
                                    }.onFailure { error ->
                                        _state.update { s -> s.copy(isPubFavoriteLoading = false) }
                                        val errorMsg = error.message ?: ""
                                        if (errorMsg.contains("이미") ||
                                            errorMsg.contains("Conflict") ||
                                            errorMsg.contains("409")
                                        ) {
                                            loadFavoritePubIds()
                                            emit(HomeContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
                                        } else {
                                            emit(HomeContract.SideEffect.ShowToast("즐겨찾기 해제에 실패했습니다."))
                                        }
                                    }
                            } else {
                                loadFavoritePubIds()
                                _state.update { s -> s.copy(isPubFavoriteLoading = false) }
                                emit(HomeContract.SideEffect.ShowToast("잠시 후 다시 시도해주세요."))
                            }
                        } else {
                            favoriteRepository
                                .addFavorite(pubId)
                                .onSuccess { newId ->
                                    _state.update {
                                        it.copy(
                                            isPubFavoriteLoading = false,
                                            selectedPubDetail = detail.copy(
                                                isWishlisted = true,
                                                favoriteCount = detail.favoriteCount + 1,
                                            ),
                                            favoritePubIds = it.favoritePubIds + pubId,
                                            favoriteIdMap = it.favoriteIdMap + (pubId to newId),
                                            pubMapItems = it.pubMapItems.map { item ->
                                                if (item.pubId == pubId) {
                                                    item.copy(favoriteCount = item.favoriteCount + 1)
                                                } else {
                                                    item
                                                }
                                            },
                                            pubMarkers = if (currentZoom > 13.0) {
                                                it.pubMapItems
                                                    .map { item ->
                                                        if (item.pubId == pubId) {
                                                            item.copy(favoriteCount = item.favoriteCount + 1)
                                                        } else {
                                                            item
                                                        }
                                                    }.toMarkers(it.favoritePubIds + pubId)
                                            } else {
                                                emptyList()
                                            },
                                        )
                                    }
                                    emit(HomeContract.SideEffect.ShowToast("즐겨찾기에 등록되었습니다."))
                                }.onFailure { error ->
                                    _state.update { s -> s.copy(isPubFavoriteLoading = false) }
                                    val errorMsg = error.message ?: ""
                                    if (errorMsg.contains("이미") ||
                                        errorMsg.contains("Conflict") ||
                                        errorMsg.contains("409")
                                    ) {
                                        loadFavoritePubIds()
                                        emit(HomeContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
                                    } else {
                                        emit(HomeContract.SideEffect.ShowToast("즐겨찾기 등록에 실패했습니다."))
                                    }
                                }
                        }
                    }
                }

                is HomeContract.Event.OnMapBoundsChanged -> {
                    currentSwLat = event.swLat
                    currentSwLng = event.swLng
                    currentNeLat = event.neLat
                    currentNeLng = event.neLng
                    currentZoom = event.zoom

                    // 현재 적용된 필터 정보를 가져와서 지도 API 호출 시 함께 전달
                    val filter = _state.value.filter
                    val teamId = if (filter.selectedTeamIds.contains(0L)) null else filter.selectedTeamIds.firstOrNull()

                    loadMapPubs(
                        teamId = teamId,
                        openNow = filter.openNow,
                        businessDay = filter.businessDay,
                        facilityCodes = filter.facilityCodes,
                        themeCodes = filter.themeCodes,
                        foodCodes = filter.foodCodes,
                    )
                }

                is HomeContract.Event.OnPubMarkerClick ->
                    loadSelectedPubDetail(event.pubId.toLongOrNull() ?: return)

                is HomeContract.Event.OnPubListItemClick ->
                    loadSelectedPubDetail(event.pubId)

                is HomeContract.Event.OnPubListFavoriteClick -> {
                    if (_state.value.isPubFavoriteLoading) return
                    val pubId = event.pubId
                    val isWishlisted = _state.value.favoritePubIds.contains(pubId)

                    viewModelScope.launch {
                        _state.update { it.copy(isPubFavoriteLoading = true) }
                        if (isWishlisted) {
                            val favoriteId = _state.value.favoriteIdMap[pubId]
                            if (favoriteId != null) {
                                favoriteRepository
                                    .deleteFavorites(listOf(favoriteId))
                                    .onSuccess {
                                        _state.update {
                                            it.copy(
                                                isPubFavoriteLoading = false,
                                                favoritePubIds = it.favoritePubIds - pubId,
                                                favoriteIdMap = it.favoriteIdMap - pubId,
                                                pubMapItems = it.pubMapItems.map { item ->
                                                    if (item.pubId == pubId) {
                                                        item.copy(
                                                            favoriteCount = (item.favoriteCount - 1).coerceAtLeast(0),
                                                        )
                                                    } else {
                                                        item
                                                    }
                                                },
                                                pubMarkers = if (currentZoom > 13.0) {
                                                    it.pubMapItems
                                                        .map { item ->
                                                            if (item.pubId == pubId) {
                                                                item.copy(
                                                                    favoriteCount = (item.favoriteCount - 1)
                                                                        .coerceAtLeast(
                                                                            0,
                                                                        ),
                                                                )
                                                            } else {
                                                                item
                                                            }
                                                        }.toMarkers(it.favoritePubIds - pubId)
                                                } else {
                                                    emptyList()
                                                },
                                            )
                                        }
                                        emit(HomeContract.SideEffect.ShowToast("즐겨찾기에서 해제되었습니다."))
                                    }.onFailure { error ->
                                        _state.update { it.copy(isPubFavoriteLoading = false) }
                                        val errorMsg = error.message ?: ""
                                        if (errorMsg.contains("이미") ||
                                            errorMsg.contains("Conflict") ||
                                            errorMsg.contains("409")
                                        ) {
                                            loadFavoritePubIds()
                                            emit(HomeContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
                                        } else {
                                            emit(HomeContract.SideEffect.ShowToast("즐겨찾기 해제에 실패했습니다."))
                                        }
                                    }
                            } else {
                                loadFavoritePubIds()
                                _state.update { it.copy(isPubFavoriteLoading = false) }
                                emit(HomeContract.SideEffect.ShowToast("잠시 후 다시 시도해주세요."))
                            }
                        } else {
                            favoriteRepository
                                .addFavorite(pubId)
                                .onSuccess { newId ->
                                    _state.update {
                                        it.copy(
                                            isPubFavoriteLoading = false,
                                            favoritePubIds = it.favoritePubIds + pubId,
                                            favoriteIdMap = it.favoriteIdMap + (pubId to newId),
                                            pubMapItems = it.pubMapItems.map { item ->
                                                if (item.pubId == pubId) {
                                                    item.copy(favoriteCount = item.favoriteCount + 1)
                                                } else {
                                                    item
                                                }
                                            },
                                            pubMarkers = if (currentZoom > 13.0) {
                                                it.pubMapItems
                                                    .map { item ->
                                                        if (item.pubId == pubId) {
                                                            item.copy(favoriteCount = item.favoriteCount + 1)
                                                        } else {
                                                            item
                                                        }
                                                    }.toMarkers(it.favoritePubIds + pubId)
                                            } else {
                                                emptyList()
                                            },
                                        )
                                    }
                                    emit(HomeContract.SideEffect.ShowToast("즐겨찾기에 등록되었습니다."))
                                }.onFailure { error ->
                                    _state.update { it.copy(isPubFavoriteLoading = false) }
                                    val errorMsg = error.message ?: ""
                                    if (errorMsg.contains("이미") ||
                                        errorMsg.contains("Conflict") ||
                                        errorMsg.contains("409")
                                    ) {
                                        loadFavoritePubIds()
                                        emit(HomeContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
                                    } else {
                                        emit(HomeContract.SideEffect.ShowToast("즐겨찾기 등록에 실패했습니다."))
                                    }
                                }
                        }
                    }
                }

                is HomeContract.Event.OnFilterApply -> {
                    val newFilter = _state.value.filter.copy(
                        selectedTeamIds = event.teamIds,
                        selectedTeamNames = event.teamNames,
                        selectedRegions = event.regions,
                        openNow = event.openNow,
                        businessDay = event.businessDay,
                        facilityCodes = event.facilityCodes,
                        themeCodes = event.themeCodes,
                        foodCodes = event.foodCodes,
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

                    // "all" (KBO 전체) 이 포함된 경우 teamId를 null로 전달하여 전체 조회
                    val teamId = if (event.teamIds.contains(0L)) null else event.teamIds.firstOrNull()

                    loadMapPubs(
                        teamId = teamId,
                        openNow = event.openNow,
                        businessDay = event.businessDay,
                        facilityCodes = event.facilityCodes,
                        themeCodes = event.themeCodes,
                        foodCodes = event.foodCodes,
                        showEmptyToast = true,
                    )
                    loadPubList(
                        teamId = teamId,
                        openNow = event.openNow,
                        businessDay = event.businessDay,
                        facilityCodes = event.facilityCodes,
                        themeCodes = event.themeCodes,
                        foodCodes = event.foodCodes,
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
                    var newFilter = currentFilter

                    when (event.filterKey) {
                        "OPEN" -> {
                            newFilter = currentFilter.copy(openNow = if (currentFilter.openNow == true) null else true)
                        }

                        "GROUP_SEAT" -> {
                            val codes = if (currentFilter.facilityCodes?.contains("GROUP_SEAT") ==
                                true
                            ) {
                                emptyList()
                            } else {
                                listOf("GROUP_SEAT")
                            }
                            newFilter = currentFilter.copy(facilityCodes = codes)
                        }

                        "PARKING" -> {
                            val codes = if (currentFilter.facilityCodes?.contains("PARKING") ==
                                true
                            ) {
                                emptyList()
                            } else {
                                listOf("PARKING")
                            }
                            newFilter = currentFilter.copy(facilityCodes = codes)
                        }

                        "WIDE_SPACE" -> {
                            val codes = if (currentFilter.facilityCodes?.contains("SPACIOUS_AREA") ==
                                true
                            ) {
                                emptyList()
                            } else {
                                listOf("SPACIOUS_AREA")
                            }
                            newFilter = currentFilter.copy(facilityCodes = codes)
                        }

                        "VARIOUS_DRINKS" -> {
                            val codes = if (currentFilter.foodCodes?.isNotEmpty() ==
                                true
                            ) {
                                emptyList()
                            } else {
                                listOf("SOJU", "BEER", "COCKTAIL", "HIGHBALL")
                            }
                            newFilter = currentFilter.copy(foodCodes = codes)
                        }
                    }

                    _state.update { it.copy(filter = newFilter) }

                    val teamId = if (newFilter.selectedTeamIds.contains(
                            0L,
                        )
                    ) {
                        null
                    } else {
                        newFilter.selectedTeamIds.firstOrNull()
                    }

                    loadMapPubs(
                        teamId = teamId,
                        openNow = newFilter.openNow,
                        businessDay = newFilter.businessDay,
                        facilityCodes = newFilter.facilityCodes,
                        themeCodes = newFilter.themeCodes,
                        foodCodes = newFilter.foodCodes,
                        showEmptyToast = true,
                    )
                    loadPubList(
                        teamId = teamId,
                        openNow = newFilter.openNow,
                        businessDay = newFilter.businessDay,
                        facilityCodes = newFilter.facilityCodes,
                        themeCodes = newFilter.themeCodes,
                        foodCodes = newFilter.foodCodes,
                    )
                }

                is HomeContract.Event.OnClusterClick -> {
                    _state.update {
                        val clusterItems = event.cluster.items
                        it.copy(
                            showPubListSheet = clusterItems.isNotEmpty(),
                            selectedPubList = clusterItems,
                        )
                    }
                }
            }
        }

        private fun emit(effect: HomeContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
