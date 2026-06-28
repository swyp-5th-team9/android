package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserRequest(
    @SerialName("reasonCode")
    val reasonCode: String,
    @SerialName("detail")
    val detail: String? = null,
)
