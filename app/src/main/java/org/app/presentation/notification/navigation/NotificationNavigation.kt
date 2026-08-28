package org.app.presentation.notification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.notification.NotificationRoute

@Serializable
data object Notification

fun NavController.navigateToNotification() = navigate(Notification)

fun NavGraphBuilder.notificationScreen(onBack: () -> Unit) {
    composable<Notification> {
        NotificationRoute(onBack = onBack)
    }
}
