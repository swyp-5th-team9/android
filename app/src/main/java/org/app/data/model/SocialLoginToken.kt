package org.app.data.model

data class SocialLoginToken(
    val accessToken: String?,
    val refreshToken: String?,
    val role: String?,
    val onboardingCompleted: Boolean?,
    /** 탈퇴 후 30일 이내 재로그인으로 복구된 계정 여부 */
    val restored: Boolean?,
)
