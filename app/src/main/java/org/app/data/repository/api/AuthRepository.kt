package org.app.data.repository.api

import org.app.data.model.SocialLoginToken

interface AuthRepository {
    suspend fun postKakaoLogin(authorization: String): Result<SocialLoginToken>

    suspend fun postNaverLogin(authorization: String): Result<SocialLoginToken>

    suspend fun refreshToken(): Result<String>

    suspend fun logout(): Result<Unit>

    suspend fun withdraw(): Result<Unit>

    suspend fun isLoggedIn(): Boolean
}
