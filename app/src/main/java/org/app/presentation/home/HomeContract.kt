package org.app.presentation.home

import org.app.data.model.PubListItem
import org.app.data.model.PubMapItem
import org.app.presentation.home.model.HomeFilter
import org.app.presentation.home.model.PubCluster
import org.app.presentation.home.model.PubMarker
import org.app.presentation.pubdetail.model.PubDetail

interface HomeContract {
    data class State(
        val isLoading: Boolean = false,
        val pubMarkers: List<PubMarker> = emptyList(),
        val pubClusters: List<PubCluster> = emptyList(),
        val pubMapItems: List<PubMapItem> = emptyList(),
        val pubListItems: List<PubListItem> = emptyList(),
        val selectedPubList: List<PubMapItem> = emptyList(),
        val filter: HomeFilter = HomeFilter(),
        val userFavoriteTeamIds: List<Long> = emptyList(),
        val userFavoriteTeamNames: List<String> = emptyList(),
        val favoritePubIds: Set<Long> = emptySet(),
        val favoriteIdMap: Map<Long, Long> = emptyMap(), // pubId -> favoriteId
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
    }

    sealed interface Event {
        data object OnSearchBarClick : Event

        data object OnMenuFilterClick : Event

        data object OnTeamChipClick : Event

        data object OnRegionChipClick : Event

        data object OnFilterBottomSheetDismiss : Event

        data object OnReportClick : Event

        data object OnMyLocationClick : Event

        data object OnRegionSearchClick : Event

        data object OnPubDetailSheetDismiss : Event

        data object OnPubListSheetDismiss : Event

        data object OnFavoriteClick : Event

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

        data class OnFilterApply(
            val teamIds: List<Long>,
            val teamNames: List<String>,
            val regions: List<String>,
            val openNow: Boolean? = null,
            val businessDay: String? = null,
            val facilityCodes: List<String>? = null,
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
