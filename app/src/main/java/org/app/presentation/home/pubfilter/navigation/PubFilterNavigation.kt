package org.app.presentation.home.pubfilter.navigation

import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.home.pubfilter.PubFilterRoute

fun NavController.navigateToPubFilter() = navigate(PubFilter)

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
        PubFilterRoute(
            onBack = onBack,
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
