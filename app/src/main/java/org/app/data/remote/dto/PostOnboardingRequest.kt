package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostOnboardingRequest(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("teamIds")
    val teamIds: List<Long>,
)
