package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetPubsMapResponse
import org.app.data.remote.dto.GetPubsResponse
import org.app.data.remote.dto.PubDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PubService {
    @GET("/api/v1/pubs")
    suspend fun getPubs(
        @Query("keyword") keyword: String? = null,
        @Query("teamId") teamId: Long? = null,
        @Query("region") region: String? = null,
        @Query("facilityCodes") facilityCodes: List<String>? = null,
        @Query("styleCodes") styleCodes: List<String>? = null,
        @Query("themeCodes") themeCodes: List<String>? = null,
        @Query("foodCodes") foodCodes: List<String>? = null,
        @Query("capacityRange") capacityRange: String? = null,
        @Query("businessStatus") businessStatus: String? = null,
        @Query("businessDays") businessDays: List<Int>? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): BaseResponse<GetPubsResponse>

    @GET("/api/v1/pubs/map")
    suspend fun getMapPubs(
        @Query("swLat") swLat: Double,
        @Query("swLng") swLng: Double,
        @Query("neLat") neLat: Double,
        @Query("neLng") neLng: Double,
        @Query("teamId") teamId: Long? = null,
    ): BaseResponse<GetPubsMapResponse>

    @GET("/api/v1/pubs/{pubId}")
    suspend fun getPubDetail(
        @Path("pubId") pubId: Long,
    ): BaseResponse<PubDetailResponse>
}
