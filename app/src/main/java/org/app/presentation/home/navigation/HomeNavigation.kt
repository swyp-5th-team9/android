package org.app.presentation.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.MainTabRoute
import org.app.presentation.home.HomeScreen

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(Home, navOptions)

fun NavGraphBuilder.homeGraph(innerPadding: PaddingValues) {
    composable<Home> {
        HomeScreen(
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Serializable
data object Home : MainTabRoute
