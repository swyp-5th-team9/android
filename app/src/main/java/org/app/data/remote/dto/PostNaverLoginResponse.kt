package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostNaverLoginResponse(
    @SerialName("accessToken")
    val accessToken: String?,
    @SerialName("refreshToken")
    val refreshToken: String?,
    @SerialName("role")
    val role: String?,
    @SerialName("onboardingCompleted")
    val onboardingCompleted: Boolean?,
)
