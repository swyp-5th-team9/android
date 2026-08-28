package org.app.presentation.home.navigation

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.MainTabRoute
import org.app.presentation.home.HomeContract
import org.app.presentation.home.HomeRoute
import org.app.presentation.home.HomeViewModel
import org.app.presentation.home.homesearch.navigation.homeSearchScreen
import org.app.presentation.home.homesearch.navigation.navigateToHomeSearch
import org.app.presentation.home.pubfilter.navigation.navigateToPubFilter
import org.app.presentation.home.pubfilter.navigation.pubFilterScreen
import org.app.presentation.mypage.report.ReportRoute
import org.app.presentation.notification.navigation.navigateToNotification

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(HomeGraph, navOptions)

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    navigateToPubDetail: (pubId: String) -> Unit,
    onUpdateBottomBarVisible: (Boolean) -> Unit = {},
) {
    navigation<HomeGraph>(startDestination = Home) {
        composable<Home> { backStackEntry ->
            val homeViewModel: HomeViewModel = hiltViewModel()
            val savedStateHandle = backStackEntry.savedStateHandle

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        homeViewModel.onEvent(HomeContract.Event.OnRefresh)
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            val pubFilterApplied by savedStateHandle
                .getStateFlow("pub_filter_applied", false)
                .collectAsStateWithLifecycle()

            LaunchedEffect(pubFilterApplied) {
                if (pubFilterApplied) {
                    val ids: List<Long> =
                        savedStateHandle
                            .get<ArrayList<Long>>("pub_filter_team_ids")
                            ?.toList() ?: emptyList()
                    val names: List<String> =
                        savedStateHandle
                            .get<ArrayList<String>>("pub_filter_team_names")
                            ?.toList() ?: emptyList()
                    val regions: List<String> =
                        savedStateHandle
                            .get<ArrayList<String>>("pub_filter_regions")
                            ?.toList() ?: emptyList()
                    val openNow: Boolean? = savedStateHandle["pub_filter_open_now"]
                    val businessDay: String? = savedStateHandle["pub_filter_business_day"]
                    val facilityCodes: List<String> =
                        savedStateHandle
                            .get<ArrayList<String>>("pub_filter_facility_codes")
                            ?.toList() ?: emptyList()
                    val styleCodes: List<String> =
                        savedStateHandle
                            .get<ArrayList<String>>("pub_filter_style_codes")
                            ?.toList() ?: emptyList()
                    val themeCodes: List<String> =
                        savedStateHandle
                            .get<ArrayList<String>>("pub_filter_theme_codes")
                            ?.toList() ?: emptyList()
                    val foodCodes: List<String> =
                        savedStateHandle
                            .get<ArrayList<String>>("pub_filter_food_codes")
                            ?.toList() ?: emptyList()
                    homeViewModel.onEvent(
                        HomeContract.Event.OnFilterApply(
                            teamIds = ids,
                            teamNames = names,
                            regions = regions,
                            openNow = openNow,
                            businessDay = businessDay,
                            facilityCodes = facilityCodes.ifEmpty { null },
                            styleCodes = styleCodes.ifEmpty { null },
                            themeCodes = themeCodes.ifEmpty { null },
                            foodCodes = foodCodes.ifEmpty { null },
                        ),
                    )
                    savedStateHandle["pub_filter_applied"] = false
                }
            }

            HomeRoute(
                onNavigateToPubDetail = navigateToPubDetail,
                onNavigateToSearch = { navController.navigateToHomeSearch() },
                onNavigateToPubFilter = { filter -> navController.navigateToPubFilter(filter) },
                onNavigateToReport = { navController.navigate(HomeReport) },
                onNavigateToNotification = { navController.navigateToNotification() },
                viewModel = homeViewModel,
            )
        }

        pubFilterScreen(
            navController = navController,
            onBack = { navController.popBackStack() },
            onUpdateBottomBarVisible = onUpdateBottomBarVisible,
        )

        homeSearchScreen(
            onBack = { navController.popBackStack() },
            navigateToPubDetail = navigateToPubDetail,
            onUpdateBottomBarVisible = onUpdateBottomBarVisible,
        )

        composable<HomeReport> {
            DisposableEffect(Unit) {
                onUpdateBottomBarVisible(false)
                onDispose { onUpdateBottomBarVisible(true) }
            }
            ReportRoute(onBack = { navController.popBackStack() })
        }
    }
}

@Serializable
data object HomeGraph : MainTabRoute

@Serializable
data object Home

@Serializable
data object HomeReport
