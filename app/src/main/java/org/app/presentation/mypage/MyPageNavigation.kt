package org.app.presentation.mypage

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.MainTabRoute

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) = navigate(MyPage, navOptions)

fun NavGraphBuilder.myPageGraph(
    navigateToLogin: () -> Unit,
    innerPadding: PaddingValues,
) {
    composable<MyPage> {
        MyPageRoute(
            navigateToLogin = navigateToLogin,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Serializable
data object MyPage : MainTabRoute
