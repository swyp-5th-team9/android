package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostFavoriteResponse(
    @SerialName("favoriteId")
    val favoriteId: Long,
)
