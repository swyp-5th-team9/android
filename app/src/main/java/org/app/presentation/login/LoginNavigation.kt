package org.app.presentation.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.Route

fun NavController.navigateToLogin(navOptions: NavOptions? = null) = navigate(Login, navOptions)

fun NavGraphBuilder.loginGraph(
    navigateToHome: () -> Unit,
    innerPadding: PaddingValues,
) {
    composable<Login> {
        LoginRoute(
            navigateToHome = navigateToHome,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Serializable
data object Login : Route
