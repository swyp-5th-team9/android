package org.app.data.remote.service

import okhttp3.MultipartBody
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.DeleteUserRequest
import org.app.data.remote.dto.GetUserResponse
import org.app.data.remote.dto.PostOnboardingRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface UserService {
    @POST("/api/v1/users/me/onboarding")
    suspend fun postOnboarding(
        @Body body: PostOnboardingRequest,
    ): BaseResponse<Unit>

    @GET("/api/v1/users/me")
    suspend fun getUser(): BaseResponse<GetUserResponse>

    /** multipart — nickname/teamIds/profileImage, 미전달 필드는 기존 값 유지 */
    @Multipart
    @PATCH("/api/v1/users/me")
    suspend fun patchUser(
        @Part parts: List<MultipartBody.Part>,
    ): BaseResponse<Unit>

    @HTTP(method = "DELETE", path = "/api/v1/users/me", hasBody = true)
    suspend fun deleteUser(
        @Body body: DeleteUserRequest,
    ): BaseResponse<Unit>
}
