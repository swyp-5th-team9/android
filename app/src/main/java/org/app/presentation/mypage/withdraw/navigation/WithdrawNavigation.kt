package org.app.presentation.mypage.withdraw.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.mypage.withdraw.WithdrawRoute

fun NavController.navigateToWithdraw() = navigate(Withdraw)

fun NavGraphBuilder.withdrawScreen(
    onBack: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    composable<Withdraw> {
        WithdrawRoute(
            onBack = onBack,
            navigateToLogin = navigateToLogin,
        )
    }
}

@Serializable
data object Withdraw
