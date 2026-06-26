package org.app.presentation.pubdetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.Route
import org.app.presentation.pubdetail.PubDetailRoute

fun NavController.navigateToPubDetail(navOptions: NavOptions? = null) = navigate(PubDetail, navOptions)

fun NavGraphBuilder.pubDetailGraph(
    onBack: () -> Unit,
    onEditClick: () -> Unit,
) {
    composable<PubDetail> {
        PubDetailRoute(
            onBack = onBack,
            onEditClick = onEditClick,
        )
    }
}

@Serializable
data object PubDetail : Route
