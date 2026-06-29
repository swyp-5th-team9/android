package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetFavoritesResponse(
    @SerialName("favorites")
    val favorites: List<FavoriteItemResponse>,
)

@Serializable
data class FavoriteItemResponse(
    @SerialName("favoriteId")
    val favoriteId: Long,
    @SerialName("pubId")
    val pubId: Long,
    @SerialName("pubName")
    val pubName: String,
    @SerialName("thumbnailImageUrl")
    val thumbnailImageUrl: String?,
)
