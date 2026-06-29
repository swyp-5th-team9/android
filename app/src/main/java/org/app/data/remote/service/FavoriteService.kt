package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.DeleteFavoritesRequest
import org.app.data.remote.dto.GetFavoritesResponse
import org.app.data.remote.dto.PostFavoriteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteService {
    @POST("/api/v1/favorites/{pubId}")
    suspend fun postFavorite(
        @Path("pubId") pubId: Long,
    ): BaseResponse<PostFavoriteResponse>

    @GET("/api/v1/favorites")
    suspend fun getFavorites(): BaseResponse<GetFavoritesResponse>

    @POST("/api/v1/favorites/delete")
    suspend fun deleteFavorites(
        @Body body: DeleteFavoritesRequest,
    ): BaseResponse<Unit>
}
