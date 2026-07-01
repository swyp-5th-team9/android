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
                            it.copy(isLoading = false, pubMarkers = items.toMarkers(), pubClusters = emptyList())
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

                is HomeContract.Event.OnMapBoundsChanged -> {
                    currentSwLat = event.swLat
                    currentSwLng = event.swLng
                    currentNeLat = event.neLat
                    currentNeLng = event.neLng
                }

                is HomeContract.Event.OnPubMarkerClick ->
                    emit(HomeContract.SideEffect.NavigateToPubDetail(event.pubId))

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
            }
        }

        private fun emit(effect: HomeContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
