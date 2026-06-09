package org.app.data.repository.api

import android.content.Context
import org.app.data.model.KakaoLoginToken

interface AuthRepository {
    suspend fun loginKakao(context: Context): Result<String>

    suspend fun postKakaoLogin(authorization: String): Result<KakaoLoginToken>
}
