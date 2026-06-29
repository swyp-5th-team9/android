package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetTeamsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TeamService {
    @GET("/api/v1/teams")
    suspend fun getTeams(
        @Query("sportType") sportType: String? = null,
    ): BaseResponse<GetTeamsResponse>
}
