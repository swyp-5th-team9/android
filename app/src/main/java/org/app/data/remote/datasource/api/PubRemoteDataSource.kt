package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetPubsMapResponse
import org.app.data.remote.dto.GetPubsResponse
import org.app.data.remote.dto.PubDetailResponse

interface PubRemoteDataSource {
    suspend fun getPubs(
        keyword: String?,
        teamId: Long?,
        teamIds: List<Long>?,
        region: String?,
        facilityCodes: List<String>?,
        styleCodes: List<String>?,
        themeCodes: List<String>?,
        foodCodes: List<String>?,
        capacityRange: String?,
        openNow: Boolean?,
        businessDay: String?,
        page: Int?,
        size: Int?,
    ): BaseResponse<GetPubsResponse>

    suspend fun getMapPubs(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        teamId: Long? = null,
        teamIds: List<Long>? = null,
        region: String? = null,
        facilityCodes: List<String>? = null,
        styleCodes: List<String>? = null,
        themeCodes: List<String>? = null,
        foodCodes: List<String>? = null,
        capacityRange: String? = null,
        openNow: Boolean? = null,
        businessDay: String? = null,
    ): BaseResponse<GetPubsMapResponse>

    suspend fun getPubDetail(pubId: Long): BaseResponse<PubDetailResponse>
}
