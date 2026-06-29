package org.app.data.repository.api

import android.net.Uri
import org.app.data.model.ReportResult

interface ReportRepository {
    suspend fun postReport(
        category: String,
        content: String,
        pubId: Long?,
        imageUris: List<Uri>,
    ): Result<ReportResult>
}
