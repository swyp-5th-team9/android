package org.app.presentation.onboarding.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.Route
import org.app.presentation.onboarding.login.LoginRoute

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    navigate(route = Login, navOptions = navOptions)
}

fun NavGraphBuilder.loginGraph(
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
) {
    composable<Login> {
        LoginRoute(
            navigateToHome = navigateToHome,
            navigateToSignUp = navigateToSignUp,
        )
    }
}

@Serializable
data object Login : Route
