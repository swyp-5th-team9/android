package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostKakaoLoginResponse
import org.app.data.remote.dto.PostNaverLoginResponse
import org.app.data.remote.dto.PostRefreshTokenRequest
import org.app.data.remote.dto.PostRefreshTokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/api/v1/auth/login/kakao")
    suspend fun postKakaoLogin(
        @Header("Authorization")
        authorization: String,
    ): BaseResponse<PostKakaoLoginResponse>

    @POST("/api/v1/auth/login/naver")
    suspend fun postNaverLogin(
        @Header("Authorization")
        authorization: String,
    ): BaseResponse<PostNaverLoginResponse>

    @POST("/api/v1/auth/refresh-token")
    suspend fun postRefreshToken(
        @Body body: PostRefreshTokenRequest,
    ): BaseResponse<PostRefreshTokenResponse>

    @POST("/api/v1/auth/logout")
    suspend fun postLogout(
        @Header("Authorization")
        authorization: String,
    ): BaseResponse<Unit>
}
