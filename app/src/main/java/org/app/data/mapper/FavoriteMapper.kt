package org.app.data.mapper

import org.app.data.model.FavoriteItem
import org.app.data.remote.dto.FavoriteItemResponse

fun FavoriteItemResponse.toFavoriteItem(): FavoriteItem =
    FavoriteItem(
        favoriteId = favoriteId,
        pubId = pubId,
        pubName = pubName,
        address = address,
        thumbnailImageUrl = thumbnailImageUrl,
    )
