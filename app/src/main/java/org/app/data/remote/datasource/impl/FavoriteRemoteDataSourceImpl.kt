package org.app.data.remote.datasource.impl

import org.app.data.remote.datasource.api.FavoriteRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.DeleteFavoritesRequest
import org.app.data.remote.dto.GetFavoritesResponse
import org.app.data.remote.service.FavoriteService
import javax.inject.Inject

class FavoriteRemoteDataSourceImpl
    @Inject
    constructor(
        private val favoriteService: FavoriteService,
    ) : FavoriteRemoteDataSource {
        override suspend fun postFavorite(pubId: Long): BaseResponse<Long> = favoriteService.postFavorite(pubId)

        override suspend fun getFavorites(): BaseResponse<GetFavoritesResponse> = favoriteService.getFavorites()

        override suspend fun deleteFavorites(favoriteIds: List<Long>): BaseResponse<Unit> =
            favoriteService.deleteFavorites(DeleteFavoritesRequest(favoriteIds = favoriteIds))
    }
