package org.app.data.remote.datasource.impl

import org.app.data.remote.datasource.api.AuthRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostKakaoLoginResponse
import org.app.data.remote.dto.PostNaverLoginResponse
import org.app.data.remote.dto.PostRefreshTokenRequest
import org.app.data.remote.dto.PostRefreshTokenResponse
import org.app.data.remote.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSourceImpl
    @Inject
    constructor(
        private val authService: AuthService,
    ) : AuthRemoteDataSource {
        override suspend fun postKakaoLogin(accessToken: String): BaseResponse<PostKakaoLoginResponse> =
            authService.postKakaoLogin(accessToken)

        override suspend fun postNaverLogin(accessToken: String): BaseResponse<PostNaverLoginResponse> =
            authService.postNaverLogin(accessToken)

        override suspend fun postRefreshToken(refreshToken: String): BaseResponse<PostRefreshTokenResponse> =
            authService.postRefreshToken(PostRefreshTokenRequest(refreshToken))

        override suspend fun postLogout(): BaseResponse<Unit> = authService.postLogout()
    }
