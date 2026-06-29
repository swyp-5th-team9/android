package org.app.presentation.onboarding.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.onboarding.splash.SplashRoute

@Serializable
data object Splash

fun NavGraphBuilder.splashGraph(navigateToLogin: () -> Unit) {
    composable<Splash> {
        SplashRoute(navigateToLogin = navigateToLogin)
    }
}
