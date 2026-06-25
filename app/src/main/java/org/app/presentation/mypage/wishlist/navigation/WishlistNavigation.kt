package org.app.presentation.mypage.wishlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.mypage.wishlist.WishlistRoute

@Serializable
data object Wishlist

fun NavController.navigateToWishlist() = navigate(Wishlist)

fun NavGraphBuilder.wishlistScreen(
    onBack: () -> Unit,
    navigateToPubDetail: (pubId: String) -> Unit,
) {
    composable<Wishlist> {
        WishlistRoute(
            onBack = onBack,
            navigateToPubDetail = navigateToPubDetail,
        )
    }
}
