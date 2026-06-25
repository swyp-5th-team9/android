package org.app.presentation.onboarding.signup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.Route
import org.app.presentation.onboarding.signup.SignUpCompleteRoute
import org.app.presentation.onboarding.signup.SignUpNicknameRoute
import org.app.presentation.onboarding.signup.SignUpTeamSelectionRoute

@Serializable
data object SignUpNickname : Route

@Serializable
data class SignUpTeamSelection(
    val nickname: String,
) : Route

@Serializable
data class SignUpComplete(
    val nickname: String,
) : Route

fun NavController.navigateToSignUpTeamSelection(nickname: String) = navigate(SignUpTeamSelection(nickname = nickname))

fun NavController.navigateToSignUpComplete(nickname: String) = navigate(SignUpComplete(nickname = nickname))

fun NavGraphBuilder.signUpGraph(
    navController: NavController,
    navigateToHome: () -> Unit,
) {
    composable<SignUpNickname> {
        SignUpNicknameRoute(
            onBack = { navController.popBackStack() },
            navigateToTeamSelection = { nickname ->
                navController.navigateToSignUpTeamSelection(nickname)
            },
        )
    }

    composable<SignUpTeamSelection> { backStackEntry ->
        val args = backStackEntry.toRoute<SignUpTeamSelection>()
        SignUpTeamSelectionRoute(
            nickname = args.nickname,
            onBack = { navController.popBackStack() },
            navigateToComplete = {
                navController.navigateToSignUpComplete(args.nickname)
            },
        )
    }

    composable<SignUpComplete> { backStackEntry ->
        val args = backStackEntry.toRoute<SignUpComplete>()
        SignUpCompleteRoute(
            nickname = args.nickname,
            navigateToHome = navigateToHome,
        )
    }
}
