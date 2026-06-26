package org.app.presentation.mypage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.MainTabRoute
import org.app.presentation.mypage.editprofile.navigation.editProfileScreen
import org.app.presentation.mypage.editprofile.navigation.navigateToEditProfile
import org.app.presentation.mypage.report.navigation.navigateToReport
import org.app.presentation.mypage.report.navigation.reportScreen
import org.app.presentation.mypage.wishlist.navigation.navigateToWishlist
import org.app.presentation.mypage.wishlist.navigation.wishlistScreen
import org.app.presentation.mypage.withdraw.navigation.navigateToWithdraw
import org.app.presentation.mypage.withdraw.navigation.withdrawScreen
import org.app.presentation.pubdetail.navigation.navigateToPubDetail

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) = navigate(MyPageGraph, navOptions)

fun NavGraphBuilder.myPageGraph(
    navController: NavController,
    navigateToLogin: () -> Unit,
) {
    navigation<MyPageGraph>(startDestination = MyPage) {
        composable<MyPage> {
            MyPageRoute(
                navigateToLogin = navigateToLogin,
                navigateToEditProfile = { navController.navigateToEditProfile() },
                navigateToReport = { navController.navigateToReport() },
                navigateToWithdraw = { navController.navigateToWithdraw() },
                navigateToWishlist = { navController.navigateToWishlist() },
                navigateToPubDetail = { navController.navigateToPubDetail() },
            )
        }
        editProfileScreen(onBack = { navController.popBackStack() })
        reportScreen(onBack = { navController.popBackStack() })
        withdrawScreen(
            onBack = { navController.popBackStack() },
            navigateToLogin = navigateToLogin,
        )
        wishlistScreen(
            onBack = { navController.popBackStack() },
            navigateToPubDetail = { navController.navigateToPubDetail() },
        )
    }
}

@Serializable
data object MyPageGraph : MainTabRoute

@Serializable
data object MyPage : MainTabRoute
