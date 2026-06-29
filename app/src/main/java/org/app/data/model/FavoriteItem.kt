package org.app.data.model

data class FavoriteItem(
    val favoriteId: Long,
    val pubId: Long,
    val pubName: String,
    val thumbnailImageUrl: String?,
)
