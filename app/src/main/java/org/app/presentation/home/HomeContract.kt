package org.app.presentation.home

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import org.app.data.model.PubDetail
import org.app.data.model.PubListItem
import org.app.data.model.PubMapItem
import org.app.presentation.home.model.HomeFilter
import org.app.presentation.home.model.PubCluster
import org.app.presentation.home.model.PubMarker

interface HomeContract {
    data class State(
        val isLoading: Boolean = false,
        val zoom: Double = DEFAULT_ZOOM,
        val pubMarkers: ImmutableList<PubMarker> = persistentListOf(),
        val pubClusters: ImmutableList<PubCluster> = persistentListOf(),
        val pubMapItems: ImmutableList<PubMapItem> = persistentListOf(),
        val pubListItems: ImmutableList<PubListItem> = persistentListOf(),
        val selectedPubList: ImmutableList<PubMapItem> = persistentListOf(),
        // 리스트 바텀시트용 lazy 상세 캐시 (지도 API엔 영업시간이 없어, 보이는 항목만 상세를 받아 영업상태/시간 표시)
        val listPubDetails: ImmutableMap<Long, PubDetail> = persistentMapOf(),
        val filter: HomeFilter = HomeFilter(),
        val userFavoriteTeamIds: ImmutableList<Long> = persistentListOf(),
        val userFavoriteTeamNames: ImmutableList<String> = persistentListOf(),
        val favoritePubIds: ImmutableSet<Long> = persistentSetOf(),
        val favoriteIdMap: ImmutableMap<Long, Long> = persistentMapOf(), // pubId -> favoriteId
        val showFilterBottomSheet: Boolean = false,
        val filterBottomSheetTab: FilterBottomSheetTab = FilterBottomSheetTab.TEAM,
        val showPubListSheet: Boolean = false,
        val showPubDetailSheet: Boolean = false,
        val selectedPubDetail: PubDetail? = null,
        val isPubDetailLoading: Boolean = false,
        val isPubFavoriteLoading: Boolean = false,
        val selectedPubFavoriteId: Long? = null,
    ) {
        val teamChipLabel: String
            get() = when {
                filter.isTeamFilterActive -> filter.teamChipLabel
                userFavoriteTeamNames.size == 1 -> userFavoriteTeamNames.first()
                userFavoriteTeamNames.size > 1 ->
                    "${userFavoriteTeamNames.first()} 외 ${userFavoriteTeamNames.size - 1}개"
                else -> "구단"
            }

        val regionChipLabel: String
            get() = filter.regionChipLabel

        companion object {
            const val DEFAULT_ZOOM = 14.0
        }
    }

    sealed interface Event {
        data object OnSearchBarClick : Event

        data object OnMenuFilterClick : Event

        data object OnTeamChipClick : Event

        data object OnRegionChipClick : Event

        data object OnFilterBottomSheetDismiss : Event

        data object OnReportClick : Event

        data object OnRegionSearchClick : Event

        data object OnPubDetailSheetDismiss : Event

        data object OnPubListSheetDismiss : Event

        data object OnFavoriteClick : Event

        /** 화면 재진입(ON_RESUME) 시 응원 구단·찜 목록 갱신 */
        data object OnRefresh : Event

        data class OnMapBoundsChanged(
            val swLat: Double,
            val swLng: Double,
            val neLat: Double,
            val neLng: Double,
            val zoom: Double,
        ) : Event

        data class OnPubMarkerClick(
            val pubId: String,
        ) : Event

        data class OnPubListItemClick(
            val pubId: Long,
        ) : Event

        data class OnPubListFavoriteClick(
            val pubId: Long,
        ) : Event

        data class OnPubListItemAppear(
            val pubId: Long,
        ) : Event

        data class OnPubDetailCardClick(
            val pubId: Long,
        ) : Event

        data class OnFilterApply(
            val teamIds: List<Long>,
            val teamNames: List<String>,
            val regions: List<String>,
            val openNow: Boolean? = null,
            val businessDay: String? = null,
            val facilityCodes: List<String>? = null,
            val styleCodes: List<String>? = null,
            val themeCodes: List<String>? = null,
            val foodCodes: List<String>? = null,
        ) : Event

        data class OnKakaoMapClick(
            val lat: Double,
            val lng: Double,
            val name: String,
        ) : Event

        data class OnNaverMapClick(
            val lat: Double,
            val lng: Double,
            val name: String,
        ) : Event

        data class OnQuickFilterClick(
            val filterKey: String,
        ) : Event

        data class OnClusterClick(
            val cluster: PubCluster,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateToSearch : SideEffect

        data object NavigateToPubFilter : SideEffect

        data object NavigateToReport : SideEffect

        data class NavigateToPubDetail(
            val pubId: String,
        ) : SideEffect

        data class MoveCameraToBounds(
            val points: List<Pair<Double, Double>>,
        ) : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect

        data class OpenMap(
            val url: String,
            val appScheme: String,
            val webFallbackUrl: String,
        ) : SideEffect
    }
}

enum class FilterBottomSheetTab { TEAM, REGION }
