package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.local.datasource.api.LocalTokenDataSource
import org.app.data.mapper.toKakaoLoginToken
import org.app.data.mapper.toNaverLoginToken
import org.app.data.model.SocialLoginToken
import org.app.data.remote.datasource.api.AuthRemoteDataSource
import org.app.data.remote.datasource.api.KakaoAuthRemoteDataSource
import org.app.data.remote.datasource.api.NaverAuthRemoteDataSource
import org.app.data.repository.api.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val authRemoteDataSource: AuthRemoteDataSource,
        private val kakaoAuthRemoteDataSource: KakaoAuthRemoteDataSource,
        private val naverAuthRemoteDataSource: NaverAuthRemoteDataSource,
        private val localTokenDataSource: LocalTokenDataSource,
    ) : AuthRepository {
        override suspend fun postKakaoLogin(authorization: String): Result<SocialLoginToken> =
            suspendRunCatching {
                val response = authRemoteDataSource.postKakaoLogin(authorization)
                response.data?.toKakaoLoginToken()?.also { token ->
                    localTokenDataSource.setLoginType(LOGIN_TYPE_KAKAO)
                    localTokenDataSource.setAccessToken(
                        token.accessToken?.takeIf { it.isNotBlank() }
                            ?: throw IllegalArgumentException("accessToken is missing"),
                    )
                    localTokenDataSource.setRefreshToken(
                        token.refreshToken?.takeIf { it.isNotBlank() }
                            ?: throw IllegalArgumentException("refreshToken is missing"),
                    )
                } ?: throw IllegalArgumentException("response data is null")
            }

        override suspend fun postNaverLogin(authorization: String): Result<SocialLoginToken> =
            suspendRunCatching {
                val response = authRemoteDataSource.postNaverLogin(authorization)
                response.data?.toNaverLoginToken()?.also { token ->
                    localTokenDataSource.setLoginType(LOGIN_TYPE_NAVER)
                    localTokenDataSource.setAccessToken(
                        token.accessToken?.takeIf { it.isNotBlank() }
                            ?: throw IllegalArgumentException("accessToken is missing"),
                    )
                    localTokenDataSource.setRefreshToken(
                        token.refreshToken?.takeIf { it.isNotBlank() }
                            ?: throw IllegalArgumentException("refreshToken is missing"),
                    )
                } ?: throw IllegalArgumentException("response data is null")
            }

        override suspend fun refreshToken(): Result<String> =
            suspendRunCatching {
                val refreshToken = localTokenDataSource.getRefreshToken()
                    ?: throw IllegalStateException("Refresh token not found")
                val response = authRemoteDataSource.postRefreshToken(refreshToken)
                val data = response.data ?: throw IllegalArgumentException("response data is null")
                val newAccessToken = data.accessToken?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("accessToken is missing in refresh response")
                val newRefreshToken = data.refreshToken?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("refreshToken is missing in refresh response")
                localTokenDataSource.setAccessToken(newAccessToken)
                localTokenDataSource.setRefreshToken(newRefreshToken)
                newAccessToken
            }

        override suspend fun logout(): Result<Unit> =
            suspendRunCatching {
                authRemoteDataSource.postLogout()
                when (localTokenDataSource.getLoginType()) {
                    LOGIN_TYPE_KAKAO -> kakaoAuthRemoteDataSource.logoutKakao().getOrThrow()
                    LOGIN_TYPE_NAVER -> naverAuthRemoteDataSource.logoutNaver().getOrThrow()
                    else -> throw IllegalStateException("Unsupported login type: $localTokenDataSource.getLoginType()")
                }
                localTokenDataSource.clearTokens()
            }

        override suspend fun withdraw(): Result<Unit> =
            suspendRunCatching {
                when (localTokenDataSource.getLoginType()) {
                    LOGIN_TYPE_KAKAO -> kakaoAuthRemoteDataSource.withdrawKakao().getOrThrow()
                    LOGIN_TYPE_NAVER -> naverAuthRemoteDataSource.withdrawNaver().getOrThrow()
                    else -> throw IllegalStateException("Unsupported login type: $localTokenDataSource.getLoginType()")
                }
                localTokenDataSource.clearTokens()
            }

        override suspend fun isLoggedIn(): Boolean = !localTokenDataSource.getAccessToken().isNullOrBlank()

        companion object {
            private const val LOGIN_TYPE_KAKAO = "KAKAO"
            private const val LOGIN_TYPE_NAVER = "NAVER"
        }
    }
