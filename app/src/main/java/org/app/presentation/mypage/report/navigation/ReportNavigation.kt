package org.app.presentation.mypage.report.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.mypage.report.ReportRoute

fun NavController.navigateToReport() = navigate(Report)

fun NavGraphBuilder.reportScreen(onBack: () -> Unit) {
    composable<Report> {
        ReportRoute(onBack = onBack)
    }
}

@Serializable
data object Report
