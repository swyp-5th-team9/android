package org.app.presentation.home.pubfilter.navigation

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.home.model.HomeFilter
import org.app.presentation.home.pubfilter.PubFilterRoute
import org.app.presentation.home.pubfilter.PubFilterSeedKeys
import org.app.presentation.home.pubfilter.seededSelection

fun NavController.navigateToPubFilter(filter: HomeFilter) {
    currentBackStackEntry?.savedStateHandle?.apply {
        set(PubFilterSeedKeys.TEAM_IDS, ArrayList(filter.selectedTeamIds))
        set(PubFilterSeedKeys.REGIONS, ArrayList(filter.selectedRegions))
        set(PubFilterSeedKeys.OPEN_NOW, filter.openNow)
        set(PubFilterSeedKeys.BUSINESS_DAY, filter.businessDay)
        set(PubFilterSeedKeys.FACILITY_CODES, ArrayList(filter.facilityCodes ?: emptyList()))
        set(PubFilterSeedKeys.STYLE_CODES, ArrayList(filter.styleCodes ?: emptyList()))
        set(PubFilterSeedKeys.THEME_CODES, ArrayList(filter.themeCodes ?: emptyList()))
        set(PubFilterSeedKeys.FOOD_CODES, ArrayList(filter.foodCodes ?: emptyList()))
    }
    navigate(PubFilter)
}

fun NavGraphBuilder.pubFilterScreen(
    navController: NavController,
    onBack: () -> Unit,
    onUpdateBottomBarVisible: (Boolean) -> Unit = {},
) {
    composable<PubFilter> {
        DisposableEffect(Unit) {
            onUpdateBottomBarVisible(false)
            onDispose { onUpdateBottomBarVisible(true) }
        }
        val seededOptions = remember {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.let { seededSelection(it) }
                ?: emptyMap()
        }
        PubFilterRoute(
            onBack = onBack,
            initialSelectedOptions = seededOptions,
            onApplyFilter = {
                teamIds,
                teamNames,
                regions,
                openNow,
                businessDay,
                facilityCodes,
                styleCodes,
                themeCodes,
                foodCodes,
                ->
                navController.previousBackStackEntry?.savedStateHandle?.apply {
                    set("pub_filter_team_ids", ArrayList(teamIds))
                    set("pub_filter_team_names", ArrayList(teamNames))
                    set("pub_filter_regions", ArrayList(regions))
                    set("pub_filter_open_now", openNow)
                    set("pub_filter_business_day", businessDay)
                    set("pub_filter_facility_codes", ArrayList(facilityCodes))
                    set("pub_filter_style_codes", ArrayList(styleCodes))
                    set("pub_filter_theme_codes", ArrayList(themeCodes))
                    set("pub_filter_food_codes", ArrayList(foodCodes))
                    set("pub_filter_applied", true)
                }
            },
        )
    }
}

@Serializable
data object PubFilter
