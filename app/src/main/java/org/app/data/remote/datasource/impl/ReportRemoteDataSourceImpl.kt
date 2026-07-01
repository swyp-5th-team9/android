package org.app.data.remote.datasource.impl

import android.content.Context
import android.net.Uri
import com.moball.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.app.data.remote.datasource.api.ReportRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostReportResponse
import org.app.data.remote.service.ReportService
import java.io.File
import javax.inject.Inject

class ReportRemoteDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val reportService: ReportService,
    ) : ReportRemoteDataSource {
        override suspend fun postReport(
            category: String,
            content: String,
            pubId: Long?,
            imageUris: List<Uri>,
        ): BaseResponse<PostReportResponse> {
            if (BuildConfig.USE_MOCK_SERVER) {
                return BaseResponse(
                    success = true,
                    data = PostReportResponse(reportId = (1000L..9999L).random()),
                )
            }

            val categoryPart = category.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val pubIdPart = pubId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageParts = imageUris.mapNotNull { uri ->
                val file = uriToTempFile(uri) ?: return@mapNotNull null
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestBody)
            }

            return reportService.postReport(
                category = categoryPart,
                content = contentPart,
                pubId = pubIdPart,
                images = imageParts,
            )
        }

        private fun uriToTempFile(uri: Uri): File? =
            runCatching {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return null
                val mimeType = context.contentResolver.getType(uri)
                val ext = mimeType?.substringAfterLast("/") ?: "jpg"
                val tempFile = File.createTempFile("report_img_", ".$ext", context.cacheDir)
                tempFile.outputStream().use { out -> inputStream.copyTo(out) }
                tempFile
            }.getOrNull()
    }
