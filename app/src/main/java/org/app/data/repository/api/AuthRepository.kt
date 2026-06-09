package org.app.data.repository.api

import org.app.data.model.KakaoLoginToken

interface AuthRepository {
    suspend fun postKakaoLogin(authorization: String): Result<KakaoLoginToken>

    suspend fun logoutKakao(): Result<Unit>

    suspend fun withdrawKakao(): Result<Unit>
}
