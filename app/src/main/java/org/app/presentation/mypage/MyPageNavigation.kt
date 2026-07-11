package org.app.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        composable<MyPage> { backStackEntry ->
            // 내정보 수정 화면에서 돌아오면 프로필 이미지를 강제 리로드하기 위한 플래그
            val profileUpdated by backStackEntry.savedStateHandle
                .getStateFlow("profile_updated", false)
                .collectAsStateWithLifecycle()
            MyPageRoute(
                navigateToLogin = navigateToLogin,
                navigateToEditProfile = { navController.navigateToEditProfile() },
                navigateToReport = { navController.navigateToReport() },
                navigateToWithdraw = { navController.navigateToWithdraw() },
                navigateToWishlist = { navController.navigateToWishlist() },
                navigateToPubDetail = { pubId: String -> navController.navigateToPubDetail(pubId) },
                profileUpdated = profileUpdated,
                onProfileUpdateConsumed = { backStackEntry.savedStateHandle["profile_updated"] = false },
            )
        }
        editProfileScreen(
            onBack = {
                navController.previousBackStackEntry?.savedStateHandle?.set("profile_updated", true)
                navController.popBackStack()
            },
        )
        reportScreen(onBack = { navController.popBackStack() })
        withdrawScreen(
            onBack = { navController.popBackStack() },
            navigateToLogin = navigateToLogin,
        )
        wishlistScreen(
            onBack = { navController.popBackStack() },
            navigateToPubDetail = { pubId: String -> navController.navigateToPubDetail(pubId) },
        )
    }
}

@Serializable
data object MyPageGraph : MainTabRoute

@Serializable
data object MyPage : MainTabRoute
