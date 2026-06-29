package org.app.data.mapper

import org.app.data.model.ReportResult
import org.app.data.remote.dto.PostReportResponse

fun PostReportResponse.toReportResult(): ReportResult = ReportResult(reportId = reportId)
