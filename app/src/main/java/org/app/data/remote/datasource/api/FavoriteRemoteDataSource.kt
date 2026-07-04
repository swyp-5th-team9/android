package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetFavoritesResponse

interface FavoriteRemoteDataSource {
    suspend fun postFavorite(pubId: Long): BaseResponse<Long>

    suspend fun getFavorites(): BaseResponse<GetFavoritesResponse>

    suspend fun deleteFavorites(favoriteIds: List<Long>): BaseResponse<Unit>
}
