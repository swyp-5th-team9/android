package org.app.presentation.map.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.MainTabRoute
import org.app.presentation.map.MapScreen

fun NavController.navigateToMap(navOptions: NavOptions? = null) = navigate(Map, navOptions)

fun NavGraphBuilder.mapGraph(innerPadding: PaddingValues) {
    composable<Map> {
        MapScreen(
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Serializable
data object Map : MainTabRoute
