package org.app.presentation.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.core.network.isHttpConflict
import org.app.data.mapper.toPartialDetail
import org.app.data.model.PubMapItem
import org.app.data.repository.api.FavoriteRepository
import org.app.data.repository.api.PubRepository
import org.app.data.repository.api.UserRepository
import org.app.presentation.home.model.HomeFilter
import org.app.presentation.home.model.PubCluster
import org.app.presentation.home.model.PubMarker
import org.app.presentation.home.model.PubMarkerType
import org.app.presentation.home.model.RegionMapper
import org.app.presentation.home.model.SeoulRegion
import org.app.presentation.home.pubfilter.FacilityCode
import org.app.presentation.home.pubfilter.FoodCode
import timber.log.Timber
import javax.inject.Inject

private const val DEFAULT_SW_LAT = 37.413294
private const val DEFAULT_SW_LNG = 126.734086
private const val DEFAULT_NE_LAT = 37.715133
private const val DEFAULT_NE_LNG = 127.269311

/** 이 줌 레벨 이하에서는 개별 마커 대신 클러스터를 표시 */
private const val CLUSTER_ZOOM_THRESHOLD = 13.0

/** 클러스터링 그리드 크기 (위경도 단위) */
private const val CLUSTER_GRID_SIZE = 0.05

// 외부 지도 앱 딥링크 스킴 및 웹 폴백
private const val KAKAO_MAP_SCHEME = "kakaomap"
private const val NAVER_MAP_SCHEME = "nmap"
private const val NAVER_MAP_APP_NAME = "org.app"

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val pubRepository: PubRepository,
        private val favoriteRepository: FavoriteRepository,
    ) : BaseViewModel<HomeContract.State, HomeContract.Event, HomeContract.SideEffect>(
            HomeContract.State(),
        ) {
        // 현재 지도 BBox (OnMapBoundsChanged 로 업데이트) — API 질의 파라미터일 뿐 UI 상태가 아님
        private var currentSwLat = DEFAULT_SW_LAT
        private var currentSwLng = DEFAULT_SW_LNG
        private var currentNeLat = DEFAULT_NE_LAT
        private var currentNeLng = DEFAULT_NE_LNG

        private var loadMapPubsJob: Job? = null

        // 리스트 바텀시트에서 이미 상세를 요청 중인 pubId (중복 요청 방지)
        private val loadingListDetailIds = mutableSetOf<Long>()

        init {
            loadUserFavoriteTeams()
            loadMapPubs(currentState.filter)
            loadPubList(currentState.filter)
            loadFavoritePubIds()
        }

        override fun onEvent(event: HomeContract.Event) {
            when (event) {
                HomeContract.Event.OnSearchBarClick ->
                    postSideEffect(HomeContract.SideEffect.NavigateToSearch)

                HomeContract.Event.OnMenuFilterClick ->
                    postSideEffect(HomeContract.SideEffect.NavigateToPubFilter)

                HomeContract.Event.OnTeamChipClick ->
                    setState {
                        copy(showFilterBottomSheet = true, filterBottomSheetTab = FilterBottomSheetTab.TEAM)
                    }

                HomeContract.Event.OnRegionChipClick ->
                    setState {
                        copy(showFilterBottomSheet = true, filterBottomSheetTab = FilterBottomSheetTab.REGION)
                    }

                HomeContract.Event.OnFilterBottomSheetDismiss ->
                    setState { copy(showFilterBottomSheet = false) }

                HomeContract.Event.OnReportClick ->
                    postSideEffect(HomeContract.SideEffect.NavigateToReport)

                HomeContract.Event.OnRegionSearchClick ->
                    loadMapPubs(currentState.filter, showEmptyToast = true)

                HomeContract.Event.OnPubDetailSheetDismiss ->
                    setState {
                        copy(
                            showPubDetailSheet = false,
                            selectedPubDetail = null,
                            selectedPubFavoriteId = null,
                            isPubFavoriteLoading = false,
                        )
                    }

                HomeContract.Event.OnPubListSheetDismiss ->
                    setState { copy(showPubListSheet = false, selectedPubList = emptyList()) }

                HomeContract.Event.OnFavoriteClick -> {
                    val detail = currentState.selectedPubDetail ?: return
                    toggleFavorite(pubId = detail.pubId, updateSelectedDetail = true)
                }

                HomeContract.Event.OnRefresh -> {
                    loadUserFavoriteTeams()
                    loadFavoritePubIds()
                }

                is HomeContract.Event.OnMapBoundsChanged -> {
                    currentSwLat = event.swLat
                    currentSwLng = event.swLng
                    currentNeLat = event.neLat
                    currentNeLng = event.neLng
                    setState { copy(zoom = event.zoom).withOverlays() }

                    loadMapPubs(currentState.filter)
                }

                is HomeContract.Event.OnPubMarkerClick ->
                    loadSelectedPubDetail(event.pubId.toLongOrNull() ?: return)

                is HomeContract.Event.OnPubListItemClick ->
                    loadSelectedPubDetail(event.pubId)

                is HomeContract.Event.OnPubListFavoriteClick ->
                    toggleFavorite(pubId = event.pubId, updateSelectedDetail = false)

                is HomeContract.Event.OnPubListItemAppear ->
                    loadListPubDetail(event.pubId)

                is HomeContract.Event.OnPubDetailCardClick ->
                    postSideEffect(HomeContract.SideEffect.NavigateToPubDetail(event.pubId.toString()))

                is HomeContract.Event.OnFilterApply -> {
                    val newFilter = currentState.filter.copy(
                        selectedTeamIds = event.teamIds,
                        selectedTeamNames = event.teamNames,
                        selectedRegions = event.regions,
                        openNow = event.openNow,
                        businessDay = event.businessDay,
                        facilityCodes = event.facilityCodes,
                        styleCodes = event.styleCodes,
                        themeCodes = event.themeCodes,
                        foodCodes = event.foodCodes,
                    )
                    setState { copy(filter = newFilter, showFilterBottomSheet = false) }

                    // 선택된 지역들의 좌표를 모아 바운즈 이동 처리
                    // "서울 전체"(SEOUL_ALL)는 특정 지점이 아니라 전체 조회이므로 카메라 이동 대상에서 제외
                    // (시청 좌표로 카메라가 튀는 문제 방지)
                    val points = event.regions
                        .filter { it != SeoulRegion.SEOUL_ALL.code }
                        .mapNotNull { RegionMapper.getLatLng(it) }
                    if (points.isNotEmpty()) {
                        postSideEffect(HomeContract.SideEffect.MoveCameraToBounds(points))
                    }

                    loadMapPubs(newFilter)
                    loadPubList(newFilter, showEmptyToast = true)
                }

                is HomeContract.Event.OnKakaoMapClick -> {
                    val kakaoFallback =
                        "https://map.kakao.com/link/map/${event.name},${event.lat},${event.lng}"
                    postSideEffect(
                        HomeContract.SideEffect.OpenMap(
                            url = "$KAKAO_MAP_SCHEME://look?p=${event.lat},${event.lng}",
                            appScheme = KAKAO_MAP_SCHEME,
                            webFallbackUrl = kakaoFallback,
                        ),
                    )
                }

                is HomeContract.Event.OnNaverMapClick -> {
                    val naverUrl =
                        "$NAVER_MAP_SCHEME://place?lat=${event.lat}&lng=${event.lng}" +
                            "&name=${event.name}&appname=$NAVER_MAP_APP_NAME"
                    postSideEffect(
                        HomeContract.SideEffect.OpenMap(
                            url = naverUrl,
                            appScheme = NAVER_MAP_SCHEME,
                            webFallbackUrl = "https://map.naver.com/v5/search/${event.name}",
                        ),
                    )
                }

                is HomeContract.Event.OnQuickFilterClick -> {
                    val newFilter = currentState.filter.applyQuickFilter(event.filterKey)
                    setState { copy(filter = newFilter) }
                    loadMapPubs(newFilter)
                    loadPubList(newFilter, showEmptyToast = true)
                }

                is HomeContract.Event.OnClusterClick ->
                    setState {
                        copy(
                            showPubListSheet = event.cluster.items.isNotEmpty(),
                            selectedPubList = event.cluster.items,
                        )
                    }
            }
        }

        // region ─── 데이터 로드 ───────────────────────────────────────────────

        private fun loadPubList(
            filter: HomeFilter,
            showEmptyToast: Boolean = false,
        ) {
            viewModelScope.launch {
                pubRepository
                    .getPubs(
                        teamIds = filter.teamIdsForQuery,
                        regions = filter.regionsForQuery,
                        openNow = filter.openNow,
                        businessDay = filter.businessDay,
                        facilityCodes = filter.facilityCodes,
                        styleCodes = filter.styleCodes,
                        themeCodes = filter.themeCodes,
                        foodCodes = filter.foodCodes,
                        size = 50,
                    ).onSuccess { page ->
                        setState { copy(pubListItems = page.content) }
                        // 필터 결과(bbox 무관)가 비면 안내 토스트
                        if (showEmptyToast && page.content.isEmpty()) {
                            postSideEffect(HomeContract.SideEffect.ShowToast("해당 조건에 맞는 펍이 없습니다."))
                        }
                    }.onFailure { Timber.e("펍 목록 로드 실패: $it") }
            }
        }

        private fun loadUserFavoriteTeams() {
            viewModelScope.launch {
                userRepository
                    .getUser()
                    .onSuccess { user ->
                        setState {
                            copy(
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
                        setState {
                            copy(favoritePubIds = idMap.keys, favoriteIdMap = idMap).withOverlays()
                        }
                    }.onFailure { Timber.e("찜 목록 로드 실패: $it") }
            }
        }

        private fun loadMapPubs(
            filter: HomeFilter,
            showEmptyToast: Boolean = false,
        ) {
            // 카메라 이동 등으로 연속 호출될 때 이전 요청을 취소해 응답 순서 역전을 방지
            loadMapPubsJob?.cancel()
            loadMapPubsJob = viewModelScope.launch {
                setState { copy(isLoading = true) }
                pubRepository
                    .getMapPubs(
                        swLat = currentSwLat,
                        swLng = currentSwLng,
                        neLat = currentNeLat,
                        neLng = currentNeLng,
                        teamIds = filter.teamIdsForQuery,
                        regions = filter.regionsForQuery,
                        openNow = filter.openNow,
                        businessDay = filter.businessDay,
                        facilityCodes = filter.facilityCodes,
                        styleCodes = filter.styleCodes,
                        themeCodes = filter.themeCodes,
                        foodCodes = filter.foodCodes,
                    ).onSuccess { items ->
                        setState { copy(isLoading = false, pubMapItems = items).withOverlays() }
                        if (showEmptyToast && items.isEmpty()) {
                            postSideEffect(HomeContract.SideEffect.ShowToast("해당 조건에 맞는 펍이 없습니다."))
                        }
                    }.onFailure { error ->
                        Timber.e("지도 펍 로드 실패: $error")
                        setState { copy(isLoading = false) }
                    }
            }
        }

        private fun loadSelectedPubDetail(pubId: Long) {
            viewModelScope.launch {
                val existingItem = currentState.pubMapItems.find { it.pubId == pubId }
                val partialDetail = existingItem?.toPartialDetail()

                setState {
                    copy(
                        isPubDetailLoading = true,
                        showPubDetailSheet = true,
                        showPubListSheet = false,
                        selectedPubDetail = partialDetail ?: selectedPubDetail,
                    )
                }

                pubRepository
                    .getPubDetail(pubId)
                    .onSuccess { detail ->
                        val isWishlisted = currentState.favoritePubIds.contains(pubId)
                        setState {
                            copy(
                                isPubDetailLoading = false,
                                selectedPubDetail = detail.copy(isWishlisted = isWishlisted),
                            )
                        }
                    }.onFailure { error ->
                        Timber.e("펍 상세 로드 실패: $error")
                        setState {
                            copy(
                                isPubDetailLoading = false,
                                // 이미 데이터가 있으면(partial) 시트를 닫지 않음
                                showPubDetailSheet = selectedPubDetail != null,
                                showPubListSheet = pubMapItems.isNotEmpty() && selectedPubDetail == null,
                            )
                        }
                        postSideEffect(HomeContract.SideEffect.ShowToast("펍 정보를 불러오지 못했습니다."))
                    }
            }
        }

        /**
         * 리스트 바텀시트에서 화면에 보이는 항목의 상세(businessHours)를 lazy 로드해 캐시한다.
         * 지도 API가 영업시간을 주지 않아, 보이는 펍만 상세를 받아 영업상태/영업시간을 정확히 표시하기 위함.
         * 이미 캐시됐거나 요청 중이면 스킵한다.
         */
        private fun loadListPubDetail(pubId: Long) {
            if (currentState.listPubDetails.containsKey(pubId)) return
            if (!loadingListDetailIds.add(pubId)) return
            viewModelScope.launch {
                pubRepository
                    .getPubDetail(pubId)
                    .onSuccess { detail ->
                        setState { copy(listPubDetails = listPubDetails + (pubId to detail)) }
                    }
                loadingListDetailIds.remove(pubId)
            }
        }

        // endregion

        // region ─── 즐겨찾기 토글 ─────────────────────────────────────────────

        /**
         * 즐겨찾기 등록/해제 공통 처리.
         *
         * @param updateSelectedDetail 상세 시트가 열려 있을 때 [HomeContract.State.selectedPubDetail]도 갱신할지 여부
         */
        private fun toggleFavorite(
            pubId: Long,
            updateSelectedDetail: Boolean,
        ) {
            if (currentState.isPubFavoriteLoading) return
            val isWishlisted = currentState.favoritePubIds.contains(pubId)

            viewModelScope.launch {
                setState { copy(isPubFavoriteLoading = true) }
                if (isWishlisted) {
                    val favoriteId = currentState.favoriteIdMap[pubId]
                    if (favoriteId == null) {
                        loadFavoritePubIds()
                        setState { copy(isPubFavoriteLoading = false) }
                        postSideEffect(HomeContract.SideEffect.ShowToast("잠시 후 다시 시도해주세요."))
                        return@launch
                    }
                    favoriteRepository
                        .deleteFavorites(listOf(favoriteId))
                        .onSuccess {
                            applyFavoriteResult(pubId, isFavorite = false, updateSelectedDetail = updateSelectedDetail)
                            postSideEffect(HomeContract.SideEffect.ShowToast("즐겨찾기에서 해제되었습니다."))
                        }.onFailure { error ->
                            handleFavoriteFailure(error, "즐겨찾기 해제에 실패했습니다.")
                        }
                } else {
                    favoriteRepository
                        .addFavorite(pubId)
                        .onSuccess { newId ->
                            applyFavoriteResult(
                                pubId = pubId,
                                isFavorite = true,
                                updateSelectedDetail = updateSelectedDetail,
                                newFavoriteId = newId,
                            )
                            postSideEffect(HomeContract.SideEffect.ShowToast("즐겨찾기에 등록되었습니다."))
                        }.onFailure { error ->
                            handleFavoriteFailure(error, "즐겨찾기 등록에 실패했습니다.")
                        }
                }
            }
        }

        /** 즐겨찾기 성공 결과를 상태에 반영 — 파생 상태(pubMarkers/pubClusters)는 [withOverlays]가 일괄 재계산 */
        private fun applyFavoriteResult(
            pubId: Long,
            isFavorite: Boolean,
            updateSelectedDetail: Boolean,
            newFavoriteId: Long? = null,
        ) {
            setState {
                val delta = if (isFavorite) 1 else -1
                val newDetail = selectedPubDetail?.takeIf { updateSelectedDetail && it.pubId == pubId }?.let {
                    it.copy(
                        isWishlisted = isFavorite,
                        favoriteCount = (it.favoriteCount + delta).coerceAtLeast(0),
                    )
                } ?: selectedPubDetail

                copy(
                    isPubFavoriteLoading = false,
                    selectedPubDetail = newDetail,
                    favoritePubIds = if (isFavorite) favoritePubIds + pubId else favoritePubIds - pubId,
                    favoriteIdMap = if (isFavorite && newFavoriteId != null) {
                        favoriteIdMap + (pubId to newFavoriteId)
                    } else {
                        favoriteIdMap - pubId
                    },
                    pubMapItems = pubMapItems.map { item ->
                        if (item.pubId == pubId) {
                            item.copy(favoriteCount = (item.favoriteCount + delta).coerceAtLeast(0))
                        } else {
                            item
                        }
                    },
                ).withOverlays()
            }
        }

        private fun handleFavoriteFailure(
            error: Throwable,
            defaultMessage: String,
        ) {
            setState { copy(isPubFavoriteLoading = false) }
            if (error.isHttpConflict()) {
                loadFavoritePubIds()
                postSideEffect(HomeContract.SideEffect.ShowToast("이미 즐겨찾기한 펍입니다."))
            } else {
                Timber.e(error, "즐겨찾기 변경 실패")
                postSideEffect(HomeContract.SideEffect.ShowToast(defaultMessage))
            }
        }

        // endregion

        // region ─── 파생 상태 계산 ────────────────────────────────────────────

        /**
         * pubMapItems / favoritePubIds / zoom 으로부터 지도 오버레이(마커·클러스터)를 재계산합니다.
         * 파생 상태 갱신은 반드시 이 함수를 통해서만 이루어져야 합니다.
         */
        private fun HomeContract.State.withOverlays(): HomeContract.State =
            if (zoom > CLUSTER_ZOOM_THRESHOLD) {
                copy(pubMarkers = pubMapItems.toMarkers(favoritePubIds), pubClusters = emptyList())
            } else {
                copy(pubMarkers = emptyList(), pubClusters = pubMapItems.toClusters())
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

        private fun List<PubMapItem>.toClusters(): List<PubCluster> {
            if (isEmpty()) return emptyList()

            return groupBy {
                (it.latitude / CLUSTER_GRID_SIZE).toInt() to (it.longitude / CLUSTER_GRID_SIZE).toInt()
            }.map { (_, group) ->
                PubCluster(
                    latitude = group.map { it.latitude }.average(),
                    longitude = group.map { it.longitude }.average(),
                    count = group.size,
                    items = group,
                )
            }
        }
    }

/** "all"(KBO 전체, id=0)이 포함되면 전체 조회(null), 아니면 선택 구단 전체를 OR 매칭으로 전달 */
private val HomeFilter.teamIdsForQuery: List<Long>?
    get() = if (selectedTeamIds.contains(0L)) {
        null
    } else {
        selectedTeamIds.ifEmpty { null }
    }

/**
 * 선택된 지역을 서버 regions(복수) 파라미터로 전달한다.
 * 서버가 regions를 우선 처리하므로 다중/단일 모두 regions로 보낸다(regions=A&regions=B).
 * "서울 전체"(SEOUL_ALL)는 전체 조회이므로 제외하고, 선택이 없으면 null.
 */
private val HomeFilter.regionsForQuery: List<String>?
    get() = (selectedRegions - SeoulRegion.SEOUL_ALL.code).ifEmpty { null }

/** 퀵 필터 칩 토글 규칙 */
private fun HomeFilter.applyQuickFilter(filterKey: String): HomeFilter =
    when (filterKey) {
        "OPEN" -> copy(openNow = if (openNow == true) null else true)

        "GROUP_SEAT" -> toggleFacility(FacilityCode.GROUP_SEAT.code)

        "PARKING" -> toggleFacility(FacilityCode.PARKING.code)

        "WIDE_SPACE" -> toggleFacility(FacilityCode.SPACIOUS_AREA.code)

        "VARIOUS_DRINKS" ->
            copy(
                foodCodes = if (foodCodes?.isNotEmpty() == true) {
                    emptyList()
                } else {
                    listOf(FoodCode.SOJU.code, FoodCode.BEER.code, FoodCode.COCKTAIL.code, FoodCode.HIGHBALL.code)
                },
            )

        else -> this
    }

private fun HomeFilter.toggleFacility(code: String): HomeFilter {
    val current = facilityCodes?.toMutableList() ?: mutableListOf()
    if (current.contains(code)) current.remove(code) else current.add(code)
    return copy(facilityCodes = if (current.isEmpty()) null else current)
}
