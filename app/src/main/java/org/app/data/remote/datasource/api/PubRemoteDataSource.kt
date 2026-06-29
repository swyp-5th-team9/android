package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetPubsMapResponse
import org.app.data.remote.dto.GetPubsResponse
import org.app.data.remote.dto.PubDetailResponse

interface PubRemoteDataSource {
    suspend fun getPubs(
        keyword: String?,
        teamId: Long?,
        region: String?,
        facilityCodes: List<String>?,
        styleCodes: List<String>?,
        themeCodes: List<String>?,
        foodCodes: List<String>?,
        capacityRange: String?,
        businessStatus: String?,
        businessDays: List<Int>?,
        sort: String?,
        page: Int?,
        size: Int?,
    ): BaseResponse<GetPubsResponse>

    suspend fun getMapPubs(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        teamId: Long?,
    ): BaseResponse<GetPubsMapResponse>

    suspend fun getPubDetail(pubId: Long): BaseResponse<PubDetailResponse>
}
