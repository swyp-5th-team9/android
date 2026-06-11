package org.app.presentation.mypage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.MainTabRoute

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) = navigate(MyPage, navOptions)

fun NavGraphBuilder.myPageGraph(navigateToLogin: () -> Unit) {
    composable<MyPage> {
        MyPageRoute(
            navigateToLogin = navigateToLogin,
        )
    }
}

@Serializable
data object MyPage : MainTabRoute
