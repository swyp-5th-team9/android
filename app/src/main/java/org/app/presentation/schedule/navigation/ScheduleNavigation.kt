package org.app.presentation.schedule.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.core.common.navigation.MainTabRoute
import org.app.presentation.schedule.ScheduleRoute

fun NavController.navigateToSchedule(navOptions: NavOptions? = null) = navigate(Schedule, navOptions)

fun NavGraphBuilder.scheduleGraph() {
    composable<Schedule> {
        ScheduleRoute()
    }
}

@Serializable
data object Schedule : MainTabRoute
