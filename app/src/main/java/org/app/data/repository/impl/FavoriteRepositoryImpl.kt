package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.mapper.toFavoriteItem
import org.app.data.model.FavoriteItem
import org.app.data.remote.datasource.api.FavoriteRemoteDataSource
import org.app.data.repository.api.FavoriteRepository
import javax.inject.Inject

class FavoriteRepositoryImpl
    @Inject
    constructor(
        private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
    ) : FavoriteRepository {
        override suspend fun addFavorite(pubId: Long): Result<Long> =
            suspendRunCatching {
                val response = favoriteRemoteDataSource.postFavorite(pubId)
                response.data?.favoriteId
                    ?: throw IllegalStateException("favoriteId is null")
            }

        override suspend fun getFavorites(): Result<List<FavoriteItem>> =
            suspendRunCatching {
                val favorites = favoriteRemoteDataSource.getFavorites().data?.favorites
                    ?: throw IllegalStateException("favorites is null")
                favorites.map { it.toFavoriteItem() }
            }

        override suspend fun deleteFavorites(favoriteIds: List<Long>): Result<Unit> =
            suspendRunCatching {
                require(favoriteIds.isNotEmpty()) { "삭제할 즐겨찾기를 선택해주세요." }
                favoriteRemoteDataSource.deleteFavorites(favoriteIds).let {}
            }
    }
