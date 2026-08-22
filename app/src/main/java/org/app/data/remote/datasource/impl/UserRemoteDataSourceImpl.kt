package org.app.data.remote.datasource.impl

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.app.core.util.ImageCompressor
import org.app.data.remote.datasource.api.UserRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.DeleteUserRequest
import org.app.data.remote.dto.GetUserResponse
import org.app.data.remote.dto.PostFcmTokenRequest
import org.app.data.remote.dto.PostOnboardingRequest
import org.app.data.remote.service.UserService
import timber.log.Timber
import java.io.File
import javax.inject.Inject

private const val PROFILE_IMAGE_MAX_DIMENSION = 512

class UserRemoteDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val userService: UserService,
    ) : UserRemoteDataSource {
        override suspend fun postOnboarding(
            nickname: String,
            teamIds: List<Long>,
        ): BaseResponse<Unit> =
            userService.postOnboarding(PostOnboardingRequest(nickname = nickname, teamIds = teamIds))

        override suspend fun getUser(): BaseResponse<GetUserResponse> = userService.getUser()

        override suspend fun patchUser(
            nickname: String?,
            teamIds: List<Long>?,
            profileImageUri: String?,
        ): BaseResponse<Unit> {
            var tempFile: File? = null
            return try {
                val parts = buildList {
                    nickname?.let { add(MultipartBody.Part.createFormData("nickname", it)) }
                    // null = 미전달(기존 값 유지). 빈 배열([]) = 전체 해제 —
                    // multipart는 part가 0개면 전송이 불가하므로 빈 값 part 하나로 "해제"를 표현한다.
                    teamIds?.let { ids ->
                        if (ids.isEmpty()) {
                            add(MultipartBody.Part.createFormData("teamIds", ""))
                        } else {
                            ids.forEach { add(MultipartBody.Part.createFormData("teamIds", it.toString())) }
                        }
                    }
                    profileImageUri?.let { uri ->
                        val file = withContext(Dispatchers.IO) {
                            val temp = File.createTempFile("profile_upload_", ".jpg", context.cacheDir)
                            ImageCompressor.compress(
                                context = context,
                                uri = uri.toUri(),
                                targetFile = temp,
                                maxDimension = PROFILE_IMAGE_MAX_DIMENSION,
                            )
                        }
                        tempFile = file
                        add(
                            MultipartBody.Part.createFormData(
                                "profileImage",
                                file.name,
                                file.asRequestBody("image/jpeg".toMediaTypeOrNull()),
                            ),
                        )
                    }
                }
                Timber.d(
                    "PATCH /users/me — nickname=%s, teamIds=%s, image=%s",
                    nickname,
                    teamIds,
                    tempFile?.let { "${it.name} (${it.length()} bytes)" } ?: "없음",
                )
                userService.patchUser(parts)
            } finally {
                withContext(Dispatchers.IO) { tempFile?.delete() }
            }
        }

        override suspend fun deleteUser(
            reasonCode: String,
            detail: String?,
        ): BaseResponse<Unit> = userService.deleteUser(DeleteUserRequest(reasonCode = reasonCode, detail = detail))

        override suspend fun postFcmToken(token: String): BaseResponse<Unit> =
            userService.postFcmToken(PostFcmTokenRequest(token = token))
    }
