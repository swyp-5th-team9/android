package org.app.presentation.mypage.favorite.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.mypage.favorite.FavoriteRoute

@Serializable
data object Favorite

fun NavController.navigateToFavorite() = navigate(Favorite)

fun NavGraphBuilder.favoriteScreen(
    onBack: () -> Unit,
    navigateToPubDetail: (pubId: String) -> Unit,
) {
    composable<Favorite> {
        FavoriteRoute(
            onBack = onBack,
            navigateToPubDetail = navigateToPubDetail,
            // TODO: pubId 를 포함한 경로로 이동하는 로직 구현 필요
        )
    }
}
