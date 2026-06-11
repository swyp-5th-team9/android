package org.app.presentation.home.pubdetail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.Route
import org.app.presentation.home.pubdetail.PubDetailRoute

fun NavController.navigateToPubDetail(navOptions: NavOptions? = null) = navigate(PubDetail, navOptions)

fun NavGraphBuilder.pubDetailGraph(innerPadding: PaddingValues) {
    composable<PubDetail> {
        PubDetailRoute(
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Serializable
data object PubDetail : Route
