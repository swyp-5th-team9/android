package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostKakaoLoginResponse
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/api/v1/auth/login/kakao")
    suspend fun postKakaoLogin(
        @Header("Authorization")
        authorization: String,
    ): BaseResponse<PostKakaoLoginResponse>
}
