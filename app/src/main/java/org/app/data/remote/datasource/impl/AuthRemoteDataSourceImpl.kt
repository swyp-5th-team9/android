package org.app.data.remote.datasource.impl

import com.moball.app.BuildConfig
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
        override suspend fun postKakaoLogin(accessToken: String): BaseResponse<PostKakaoLoginResponse> {
            // TODO: 서버 배포 후 USE_MOCK_SERVER = false 로 변경
            if (BuildConfig.USE_MOCK_SERVER) {
                return BaseResponse(
                    status = "success_mock",
                    statusCode = 200,
                    data = PostKakaoLoginResponse(
                        accessToken = "fake_kakao_access_token",
                        refreshToken = "fake_kakao_refresh_token",
                        role = "FAN",
                        onboardingCompleted = false,
                    ),
                    timestamp = "",
                )
            }
            return authService.postKakaoLogin(accessToken)
        }

        override suspend fun postNaverLogin(accessToken: String): BaseResponse<PostNaverLoginResponse> {
            // TODO: 서버 배포 후 USE_MOCK_SERVER = false 로 변경
            if (BuildConfig.USE_MOCK_SERVER) {
                return BaseResponse(
                    status = "success_mock",
                    statusCode = 200,
                    data = PostNaverLoginResponse(
                        accessToken = "fake_naver_access_token",
                        refreshToken = "fake_naver_refresh_token",
                        role = "FAN",
                        onboardingCompleted = false,
                    ),
                    timestamp = "",
                )
            }
            return authService.postNaverLogin(accessToken)
        }

        override suspend fun postRefreshToken(refreshToken: String): BaseResponse<PostRefreshTokenResponse> {
            if (BuildConfig.USE_MOCK_SERVER) {
                return BaseResponse(
                    status = "success_mock",
                    statusCode = 200,
                    data = PostRefreshTokenResponse(
                        accessToken = "fake_new_access_token",
                        refreshToken = "fake_new_refresh_token",
                    ),
                    timestamp = "",
                )
            }
            return authService.postRefreshToken(PostRefreshTokenRequest(refreshToken))
        }

        override suspend fun postLogout(authorization: String): BaseResponse<Unit> {
            if (BuildConfig.USE_MOCK_SERVER) {
                return BaseResponse(
                    status = "success_mock",
                    statusCode = 200,
                    data = Unit,
                    timestamp = "",
                )
            }
            return authService.postLogout(authorization)
        }
    }
