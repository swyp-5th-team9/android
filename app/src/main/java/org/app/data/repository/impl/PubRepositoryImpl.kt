package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.mapper.toPubDetail
import org.app.data.mapper.toPubMapItems
import org.app.data.mapper.toPubPage
import org.app.data.model.PubMapItem
import org.app.data.model.PubPage
import org.app.data.remote.datasource.api.PubRemoteDataSource
import org.app.data.remote.dto.getDataOrThrow
import org.app.data.repository.api.PubRepository
import org.app.presentation.pubdetail.model.PubDetail
import javax.inject.Inject

class PubRepositoryImpl
    @Inject
    constructor(
        private val pubRemoteDataSource: PubRemoteDataSource,
    ) : PubRepository {
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
        ): Result<PubPage> =
            suspendRunCatching {
                pubRemoteDataSource
                    .getPubs(
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
                    ).getDataOrThrow()
                    .toPubPage()
            }

        override suspend fun getMapPubs(
            swLat: Double,
            swLng: Double,
            neLat: Double,
            neLng: Double,
            teamId: Long?,
            openNow: Boolean?,
            businessDay: String?,
            facilityCodes: List<String>?,
            themeCodes: List<String>?,
            foodCodes: List<String>?,
        ): Result<List<PubMapItem>> =
            suspendRunCatching {
                pubRemoteDataSource
                    .getMapPubs(
                        swLat,
                        swLng,
                        neLat,
                        neLng,
                        teamId,
                        openNow,
                        businessDay,
                        facilityCodes,
                        themeCodes,
                        foodCodes,
                    ).getDataOrThrow()
                    .toPubMapItems()
            }

        override suspend fun getPubDetail(pubId: Long): Result<PubDetail> =
            suspendRunCatching {
                pubRemoteDataSource
                    .getPubDetail(pubId)
                    .getDataOrThrow()
                    .toPubDetail()
            }
    }
