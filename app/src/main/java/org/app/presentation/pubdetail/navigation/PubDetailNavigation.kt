package org.app.presentation.pubdetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.Route
import org.app.presentation.pubdetail.PubDetailRoute

fun NavController.navigateToPubDetail(
    pubId: String = "",
    navOptions: NavOptions? = null,
) = navigate(PubDetail(pubId = pubId), navOptions)

fun NavGraphBuilder.pubDetailGraph(onBack: () -> Unit) {
    composable<PubDetail> { backStackEntry ->
        PubDetailRoute(
            onBack = onBack,
        )
    }
}

@Serializable
data class PubDetail(
    val pubId: String = "",
) : Route
