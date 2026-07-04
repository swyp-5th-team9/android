package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.local.datasource.api.LocalTokenDataSource
import org.app.data.mapper.toKakaoLoginToken
import org.app.data.mapper.toNaverLoginToken
import org.app.data.model.SocialLoginToken
import org.app.data.remote.datasource.api.AuthRemoteDataSource
import org.app.data.remote.datasource.api.KakaoAuthRemoteDataSource
import org.app.data.remote.datasource.api.NaverAuthRemoteDataSource
import org.app.data.remote.dto.checkSuccess
import org.app.data.remote.dto.getDataOrThrow
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
                val token = authRemoteDataSource.postKakaoLogin(authorization).getDataOrThrow().toKakaoLoginToken()
                localTokenDataSource.setLoginType(LOGIN_TYPE_KAKAO)
                localTokenDataSource.setAccessToken(
                    token.accessToken?.takeIf { it.isNotBlank() }
                        ?: throw IllegalArgumentException("accessToken is missing"),
                )
                localTokenDataSource.setRefreshToken(
                    token.refreshToken?.takeIf { it.isNotBlank() }
                        ?: throw IllegalArgumentException("refreshToken is missing"),
                )
                token
            }

        override suspend fun postNaverLogin(authorization: String): Result<SocialLoginToken> =
            suspendRunCatching {
                val token = authRemoteDataSource.postNaverLogin(authorization).getDataOrThrow().toNaverLoginToken()
                localTokenDataSource.setLoginType(LOGIN_TYPE_NAVER)
                localTokenDataSource.setAccessToken(
                    token.accessToken?.takeIf { it.isNotBlank() }
                        ?: throw IllegalArgumentException("accessToken is missing"),
                )
                localTokenDataSource.setRefreshToken(
                    token.refreshToken?.takeIf { it.isNotBlank() }
                        ?: throw IllegalArgumentException("refreshToken is missing"),
                )
                token
            }

        override suspend fun refreshToken(): Result<String> =
            suspendRunCatching {
                val refreshToken = localTokenDataSource.getRefreshToken()
                    ?: throw IllegalStateException("Refresh token not found")
                val data = authRemoteDataSource.postRefreshToken(refreshToken).getDataOrThrow()
                val newAccessToken = data.accessToken?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("accessToken is missing in refresh response")
                localTokenDataSource.setAccessToken(newAccessToken)
                // 새 refreshToken이 있을 때만 갱신, 없으면 기존 토큰 유지
                data.refreshToken?.takeIf { it.isNotBlank() }?.let { newRefreshToken ->
                    localTokenDataSource.setRefreshToken(newRefreshToken)
                }
                newAccessToken
            }

        override suspend fun logout(): Result<Unit> =
            suspendRunCatching {
                try {
                    authRemoteDataSource.postLogout().checkSuccess()
                    when (localTokenDataSource.getLoginType()) {
                        LOGIN_TYPE_KAKAO -> kakaoAuthRemoteDataSource.logoutKakao().getOrThrow()
                        LOGIN_TYPE_NAVER -> naverAuthRemoteDataSource.logoutNaver().getOrThrow()
                        else -> throw IllegalStateException(
                            "Unsupported login type: ${localTokenDataSource.getLoginType()}",
                        )
                    }
                } finally {
                    // 서버/소셜 로그아웃 실패 여부와 무관하게 로컬 토큰은 반드시 삭제
                    localTokenDataSource.clearTokens()
                }
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

        override suspend fun isLoggedIn(): Result<Boolean> =
            suspendRunCatching {
                !localTokenDataSource.getAccessToken().isNullOrBlank()
            }

        companion object {
            private const val LOGIN_TYPE_KAKAO = "KAKAO"
            private const val LOGIN_TYPE_NAVER = "NAVER"
        }
    }
