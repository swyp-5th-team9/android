package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchUserRequest(
    @SerialName("nickname")
    val nickname: String? = null,
    @SerialName("teamIds")
    val teamIds: List<Long>? = null,
)
