package org.app.data.remote.datasource.impl

import com.moball.app.BuildConfig
import org.app.data.remote.datasource.api.FavoriteRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.DeleteFavoritesRequest
import org.app.data.remote.dto.FavoriteItemResponse
import org.app.data.remote.dto.GetFavoritesResponse
import org.app.data.remote.dto.PostFavoriteResponse
import org.app.data.remote.service.FavoriteService
import javax.inject.Inject

class FavoriteRemoteDataSourceImpl
    @Inject
    constructor(
        private val favoriteService: FavoriteService,
    ) : FavoriteRemoteDataSource {
        // Mock 데이터
        private val mockFavorites = mutableListOf(
            FavoriteItemResponse(favoriteId = 1L, pubId = 12L, pubName = "시그니처 펍", thumbnailImageUrl = null),
            FavoriteItemResponse(favoriteId = 2L, pubId = 15L, pubName = "응원 펍", thumbnailImageUrl = null),
        )
        private var nextFavoriteId = 3L

        override suspend fun postFavorite(pubId: Long): BaseResponse<PostFavoriteResponse> {
            if (BuildConfig.USE_MOCK_SERVER) {
                require(mockFavorites.none { it.pubId == pubId }) { "FAVORITE_ALREADY_EXISTS" }
                require(mockFavorites.size < 30) { "FAVORITE_LIMIT_EXCEEDED" }
                val newId = nextFavoriteId++
                mockFavorites.add(
                    FavoriteItemResponse(
                        favoriteId = newId,
                        pubId = pubId,
                        pubName = "펍 $pubId",
                        thumbnailImageUrl = null,
                    ),
                )
                return BaseResponse(
                    success = true,
                    data = PostFavoriteResponse(favoriteId = newId),
                )
            }
            return favoriteService.postFavorite(pubId)
        }

        override suspend fun getFavorites(): BaseResponse<GetFavoritesResponse> {
            if (BuildConfig.USE_MOCK_SERVER) {
                return BaseResponse(
                    success = true,
                    data = GetFavoritesResponse(favorites = mockFavorites.sortedByDescending { it.favoriteId }),
                )
            }
            return favoriteService.getFavorites()
        }

        override suspend fun deleteFavorites(favoriteIds: List<Long>): BaseResponse<Unit> {
            if (BuildConfig.USE_MOCK_SERVER) {
                require(favoriteIds.isNotEmpty()) { "INVALID_REQUEST" }
                require(mockFavorites.map { it.favoriteId }.containsAll(favoriteIds)) { "FAVORITE_NOT_FOUND" }
                mockFavorites.removeAll { it.favoriteId in favoriteIds }
                return BaseResponse(success = true, data = Unit)
            }
            return favoriteService.deleteFavorites(DeleteFavoritesRequest(favoriteIds = favoriteIds))
        }
    }
