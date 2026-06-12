package org.app.presentation.home.pubdetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.Route
import org.app.presentation.home.pubdetail.PubDetailRoute

fun NavController.navigateToPubDetail(navOptions: NavOptions? = null) = navigate(PubDetail, navOptions)

fun NavGraphBuilder.pubDetailGraph() {
    composable<PubDetail> {
        PubDetailRoute()
    }
}

@Serializable
data object PubDetail : Route
