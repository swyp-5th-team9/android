package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteFavoritesRequest(
    @SerialName("favoriteIds")
    val favoriteIds: List<Long>,
)
