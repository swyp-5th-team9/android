package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.mapper.toKakaoLoginToken
import org.app.data.model.KakaoLoginToken
import org.app.data.remote.datasource.api.AuthRemoteDataSource
import org.app.data.remote.datasource.api.KakaoAuthDataSource
import org.app.data.repository.api.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val authRemoteDataSource: AuthRemoteDataSource,
        private val kakaoAuthDataSource: KakaoAuthDataSource,
    ) : AuthRepository {
        override suspend fun postKakaoLogin(authorization: String): Result<KakaoLoginToken> =
            suspendRunCatching {
                val response = authRemoteDataSource.postKakaoLogin(authorization)
                response.data?.toKakaoLoginToken()
                    ?: throw IllegalArgumentException("response data is null")
            }

        override suspend fun logoutKakao(): Result<Unit> = kakaoAuthDataSource.logoutKakao()

        override suspend fun withdrawKakao(): Result<Unit> = kakaoAuthDataSource.withdrawKakao()
    }
