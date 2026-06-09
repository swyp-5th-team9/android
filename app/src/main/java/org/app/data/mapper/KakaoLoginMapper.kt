package org.app.data.mapper

import org.app.data.model.KakaoLoginToken
import org.app.data.remote.dto.PostKakaoLoginResponse

fun PostKakaoLoginResponse.toKakaoLoginToken(): KakaoLoginToken {
    val access = accessToken?.takeIf { it.isNotBlank() }
        ?: throw IllegalArgumentException("access_token is missing")
    val refresh = refreshToken?.takeIf { it.isNotBlank() }
        ?: throw IllegalArgumentException("refresh_token is missing")

    return KakaoLoginToken(
        accessToken = access,
        refreshToken = refresh,
    )
}
