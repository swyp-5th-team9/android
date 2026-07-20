package org.app.data.mapper

import org.app.data.model.SocialLoginToken
import org.app.data.remote.dto.PostKakaoLoginResponse

fun PostKakaoLoginResponse.toKakaoLoginToken(): SocialLoginToken {
    val access = accessToken?.takeIf { it.isNotBlank() }
        ?: throw IllegalArgumentException("accessToken is missing")
    val refresh = refreshToken?.takeIf { it.isNotBlank() }
        ?: throw IllegalArgumentException("refreshToken is missing")

    return SocialLoginToken(
        accessToken = access,
        refreshToken = refresh,
        role = role,
        onboardingCompleted = onboardingCompleted,
        restored = restored,
    )
}
