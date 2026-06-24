package org.app.presentation.mypage.editprofile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.mypage.editprofile.EditProfileRoute

fun NavController.navigateToEditProfile() = navigate(EditProfile)

fun NavGraphBuilder.editProfileScreen(onBack: () -> Unit) {
    composable<EditProfile> {
        EditProfileRoute(onBack = onBack)
    }
}

@Serializable
data object EditProfile
