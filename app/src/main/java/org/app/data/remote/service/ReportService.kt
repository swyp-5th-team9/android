package org.app.data.remote.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostReportResponse
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ReportService {
    @Multipart
    @POST("/api/v1/reports")
    suspend fun postReport(
        @Part("category") category: RequestBody,
        @Part("content") content: RequestBody,
        @Part("pubId") pubId: RequestBody?,
        @Part images: List<MultipartBody.Part>,
    ): BaseResponse<PostReportResponse>
}
