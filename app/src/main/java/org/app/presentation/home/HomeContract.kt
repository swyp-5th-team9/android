package org.app.presentation.home

import org.app.presentation.home.model.HomeFilter
import org.app.presentation.home.model.PubCluster
import org.app.presentation.home.model.PubMarker

interface HomeContract {
    data class State(
        val isLoading: Boolean = false,
        val pubMarkers: List<PubMarker> = emptyList(),
        val pubClusters: List<PubCluster> = emptyList(),
        val filter: HomeFilter = HomeFilter(),
        val userFavoriteTeamIds: List<Int> = emptyList(),
        val userFavoriteTeamNames: List<String> = emptyList(),
        val showFilterBottomSheet: Boolean = false,
        val filterBottomSheetTab: FilterBottomSheetTab = FilterBottomSheetTab.TEAM,
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
            get() = filter.selectedRegion ?: "지역"
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

        data class OnMapBoundsChanged(
            val swLat: Double,
            val swLng: Double,
            val neLat: Double,
            val neLng: Double,
        ) : Event

        data class OnPubMarkerClick(
            val pubId: String,
        ) : Event

        data class OnFilterApply(
            val teamIds: List<Int>,
            val teamNames: List<String>,
            val region: String?,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateToSearch : SideEffect

        data object NavigateToPubFilter : SideEffect

        data object NavigateToReport : SideEffect

        data class NavigateToPubDetail(
            val pubId: String,
        ) : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}

enum class FilterBottomSheetTab { TEAM, REGION }
