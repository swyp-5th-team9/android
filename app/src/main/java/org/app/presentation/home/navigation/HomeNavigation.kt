package org.app.presentation.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.MainTabRoute
import org.app.presentation.home.HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(Home, navOptions)

fun NavGraphBuilder.homeGraph(navigateToPubDetail: (pubId: String) -> Unit) {
    composable<Home> {
        HomeRoute(
            onPubClick = navigateToPubDetail,
        )
    }
}

@Serializable
data object Home : MainTabRoute
