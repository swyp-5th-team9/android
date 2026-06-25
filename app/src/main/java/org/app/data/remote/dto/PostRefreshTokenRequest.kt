package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostRefreshTokenRequest(
    @SerialName("refreshToken")
    val refreshToken: String,
)
