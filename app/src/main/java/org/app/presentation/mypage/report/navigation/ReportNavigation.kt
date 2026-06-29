package org.app.presentation.mypage.report.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.app.presentation.mypage.report.ReportRoute

/** pubId: 홈/펍 상세에서 진입 시 전달, 마이페이지에서 진입 시 null */
fun NavController.navigateToReport(pubId: Long? = null) = navigate(Report(pubId = pubId))

fun NavGraphBuilder.reportScreen(onBack: () -> Unit) {
    composable<Report> {
        ReportRoute(onBack = onBack)
    }
}

@Serializable
data class Report(
    val pubId: Long? = null,
)
