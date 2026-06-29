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
import org.app.data.repository.api.UserRepository
import org.app.presentation.home.model.PubCluster
import org.app.presentation.home.model.PubMarker
import org.app.presentation.home.model.PubMarkerType
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(HomeContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<HomeContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadUserFavoriteTeams()
            loadMockMarkers()
        }

        private fun loadMockMarkers() {
            val mockMarkers = listOf(
                PubMarker(
                    pubId = "1",
                    name = "잠실 응원 펍",
                    latitude = 37.5113,
                    longitude = 127.0730,
                    type = PubMarkerType.FAVORITE,
                    isFavorite = true,
                ),
                PubMarker(
                    pubId = "2",
                    name = "신천 야구 펍",
                    latitude = 37.5100,
                    longitude = 127.0800,
                    type = PubMarkerType.MATCH,
                    isFavorite = false,
                ),
                PubMarker(
                    pubId = "3",
                    name = "종합운동장 펍",
                    latitude = 37.5150,
                    longitude = 127.0750,
                    type = PubMarkerType.MATCH,
                    isFavorite = false,
                ),
            )

            val mockClusters = listOf(
                PubCluster(
                    latitude = 37.5120,
                    longitude = 127.0900,
                    count = 14,
                ),
                PubCluster(
                    latitude = 37.5050,
                    longitude = 127.1000,
                    count = 3,
                ),
            )

            _state.update {
                it.copy(
                    pubMarkers = mockMarkers,
                    pubClusters = mockClusters,
                )
            }
        }

        /** 온보딩/마이페이지에서 응원 구단이 변경된 후 홈 탭 복귀 시 재호출 */
        fun refreshFavoriteTeams() = loadUserFavoriteTeams()

        private fun loadUserFavoriteTeams() {
            viewModelScope.launch {
                userRepository
                    .getUser()
                    .onSuccess { user ->
                        _state.update {
                            it.copy(
                                userFavoriteTeamIds = user.favoriteTeams.map { t -> t.teamId.toInt() },
                                userFavoriteTeamNames = user.favoriteTeams.map { t -> t.teamName },
                            )
                        }
                    }.onFailure { error ->
                        Timber.e("응원 구단 로드 실패: $error")
                    }
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
                        it.copy(
                            showFilterBottomSheet = true,
                            filterBottomSheetTab = FilterBottomSheetTab.TEAM,
                        )
                    }

                HomeContract.Event.OnRegionChipClick ->
                    _state.update {
                        it.copy(
                            showFilterBottomSheet = true,
                            filterBottomSheetTab = FilterBottomSheetTab.REGION,
                        )
                    }

                HomeContract.Event.OnFilterBottomSheetDismiss ->
                    _state.update { it.copy(showFilterBottomSheet = false) }

                HomeContract.Event.OnReportClick ->
                    emit(HomeContract.SideEffect.NavigateToReport)

                HomeContract.Event.OnMyLocationClick -> {
                    // NaverMap 위치 추적은 Screen 레이어에서 직접 처리
                }

                HomeContract.Event.OnRegionSearchClick -> {
                    // 현재 지도 영역 기반 재검색 — TODO: 서버 연결 후 구현
                    emit(HomeContract.SideEffect.ShowToast("이 지역 검색 기능은 준비 중입니다."))
                }

                is HomeContract.Event.OnPubMarkerClick ->
                    emit(HomeContract.SideEffect.NavigateToPubDetail(event.pubId))

                is HomeContract.Event.OnFilterApply -> {
                    _state.update {
                        it.copy(
                            filter = it.filter.copy(
                                selectedTeamIds = event.teamIds,
                                selectedTeamNames = event.teamNames,
                                selectedRegion = event.region,
                            ),
                            showFilterBottomSheet = false,
                        )
                    }
                    // TODO: 필터 조건으로 펍 목록 재조회
                }
            }
        }

        private fun emit(effect: HomeContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
