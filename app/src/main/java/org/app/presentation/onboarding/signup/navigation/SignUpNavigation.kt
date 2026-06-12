package org.app.presentation.onboarding.signup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.Route
import org.app.presentation.onboarding.signup.SignUpRoute

fun NavController.navigateToSignUp(navOptions: NavOptions? = null) {
    navigate(route = SignUp, navOptions = navOptions)
}

fun NavGraphBuilder.signUpGraph() {
    composable<SignUp> {
        SignUpRoute()
    }
}

@Serializable
data object SignUp : Route
