package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetFavoritesResponse
import org.app.data.remote.dto.PostFavoriteResponse

interface FavoriteRemoteDataSource {
    suspend fun postFavorite(pubId: Long): BaseResponse<PostFavoriteResponse>

    suspend fun getFavorites(): BaseResponse<GetFavoritesResponse>

    suspend fun deleteFavorites(favoriteIds: List<Long>): BaseResponse<Unit>
}
