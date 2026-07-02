package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetMatchesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MatchService {
    @GET("/api/v1/matches")
    suspend fun getMatches(
        @Query("sportType") sportType: String? = null,
        @Query("date") date: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("teamId") teamId: Long? = null,
    ): BaseResponse<GetMatchesResponse>
}
