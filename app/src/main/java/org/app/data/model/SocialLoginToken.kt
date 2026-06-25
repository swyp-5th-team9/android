package org.app.data.model

data class SocialLoginToken(
    val accessToken: String?,
    val refreshToken: String?,
    val role: String?,
    val onboardingCompleted: Boolean?,
)
