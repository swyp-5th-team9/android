package org.app.data.remote.datasource.impl

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.app.core.util.ImageCompressor
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
            val categoryPart = category.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val pubIdPart = pubId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val tempFiles = mutableListOf<File>()
            return try {
                val imageParts = withContext(Dispatchers.IO) {
                    imageUris.map { uri ->
                        val file = requireNotNull(uriToTempFile(uri)) {
                            "Failed to read report image: $uri"
                        }
                        tempFiles.add(file)
                        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("images", file.name, requestBody)
                    }
                }

                reportService.postReport(
                    category = categoryPart,
                    content = contentPart,
                    pubId = pubIdPart,
                    images = imageParts,
                )
            } finally {
                withContext(Dispatchers.IO) {
                    tempFiles.forEach { it.delete() }
                }
            }
        }

        /**
         * 원본을 그대로 복사하지 않고 리사이즈+JPEG 압축해 임시 파일로 저장한다.
         * (원본 무압축 업로드로 인한 전송 지연 방지, 서버 스펙: 파일당 10MB 이하)
         */
        private suspend fun uriToTempFile(uri: Uri): File? =
            runCatching {
                val tempFile = File.createTempFile("report_img_", ".jpg", context.cacheDir)
                ImageCompressor.compress(context = context, uri = uri, targetFile = tempFile)
            }.getOrNull()
    }
