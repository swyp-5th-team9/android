package org.app.data.remote.datasource.impl

import org.app.data.remote.datasource.api.PubRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetPubsMapResponse
import org.app.data.remote.dto.GetPubsResponse
import org.app.data.remote.dto.PubDetailResponse
import org.app.data.remote.service.PubService
import javax.inject.Inject

class PubRemoteDataSourceImpl
    @Inject
    constructor(
        private val pubService: PubService,
    ) : PubRemoteDataSource {
        override suspend fun getPubs(
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
        ): BaseResponse<GetPubsResponse> =
            pubService.getPubs(
                keyword,
                teamId,
                teamIds,
                region,
                facilityCodes,
                styleCodes,
                themeCodes,
                foodCodes,
                capacityRange,
                openNow,
                businessDay,
                page,
                size,
            )

        override suspend fun getMapPubs(
            swLat: Double,
            swLng: Double,
            neLat: Double,
            neLng: Double,
            teamId: Long?,
            openNow: Boolean?,
            businessDay: String?,
        ): BaseResponse<GetPubsMapResponse> =
            pubService.getMapPubs(swLat, swLng, neLat, neLng, teamId, openNow, businessDay)

        override suspend fun getPubDetail(pubId: Long): BaseResponse<PubDetailResponse> = pubService.getPubDetail(pubId)
    }
