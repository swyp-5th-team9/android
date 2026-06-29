package org.app.data.repository.api

import org.app.data.model.FavoriteItem

interface FavoriteRepository {
    /** 즐겨찾기 추가 → 생성된 favoriteId 반환 */
    suspend fun addFavorite(pubId: Long): Result<Long>

    /** 즐겨찾기 목록 조회 (최신순) */
    suspend fun getFavorites(): Result<List<FavoriteItem>>

    /** 즐겨찾기 일괄 삭제 */
    suspend fun deleteFavorites(favoriteIds: List<Long>): Result<Unit>
}
