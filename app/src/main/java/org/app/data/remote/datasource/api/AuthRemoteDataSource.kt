package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostKakaoLoginResponse
import org.app.data.remote.dto.PostNaverLoginResponse
import org.app.data.remote.dto.PostRefreshTokenResponse

interface AuthRemoteDataSource {
    suspend fun postKakaoLogin(accessToken: String): BaseResponse<PostKakaoLoginResponse>

    suspend fun postNaverLogin(accessToken: String): BaseResponse<PostNaverLoginResponse>

    suspend fun postRefreshToken(refreshToken: String): BaseResponse<PostRefreshTokenResponse>

    suspend fun postLogout(authorization: String): BaseResponse<Unit>
}
