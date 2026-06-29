package org.app.presentation.home.homesearch.navigation

import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.home.homesearch.HomeSearchRoute

fun NavController.navigateToHomeSearch() = navigate(HomeSearch)

fun NavGraphBuilder.homeSearchScreen(
    onBack: () -> Unit,
    navigateToPubDetail: (pubId: String) -> Unit,
    onUpdateBottomBarVisible: (Boolean) -> Unit = {},
) {
    composable<HomeSearch> {
        DisposableEffect(Unit) {
            onUpdateBottomBarVisible(false)
            onDispose { onUpdateBottomBarVisible(true) }
        }
        HomeSearchRoute(
            onBack = onBack,
            onNavigateToPubDetail = navigateToPubDetail,
        )
    }
}

@Serializable
data object HomeSearch
