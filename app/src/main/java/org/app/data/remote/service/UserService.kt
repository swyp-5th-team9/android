package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.DeleteUserRequest
import org.app.data.remote.dto.GetUserResponse
import org.app.data.remote.dto.PatchUserRequest
import org.app.data.remote.dto.PostOnboardingRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserService {
    @POST("/api/v1/users/me/onboarding")
    suspend fun postOnboarding(
        @Body body: PostOnboardingRequest,
    ): BaseResponse<Unit>

    @GET("/api/v1/users/me")
    suspend fun getUser(): BaseResponse<GetUserResponse>

    @PATCH("/api/v1/users/me")
    suspend fun patchUser(
        @Body body: PatchUserRequest,
    ): BaseResponse<Unit>

    @DELETE("/api/v1/users/me")
    suspend fun deleteUser(
        @Body body: DeleteUserRequest,
    ): BaseResponse<Unit>
}
