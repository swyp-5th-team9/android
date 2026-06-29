package org.app.data.remote.datasource.api

import android.net.Uri
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostReportResponse

interface ReportRemoteDataSource {
    suspend fun postReport(
        category: String,
        content: String,
        pubId: Long?,
        imageUris: List<Uri>,
    ): BaseResponse<PostReportResponse>
}
