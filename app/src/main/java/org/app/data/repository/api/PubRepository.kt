package org.app.data.repository.api

import org.app.data.model.PubMapItem
import org.app.data.model.PubPage
import org.app.presentation.pubdetail.model.PubDetail

interface PubRepository {
    suspend fun getPubs(
        keyword: String? = null,
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
        page: Int? = null,
        size: Int? = null,
    ): Result<PubPage>

    suspend fun getMapPubs(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        teamId: Long? = null,
        openNow: Boolean? = null,
        businessDay: String? = null,
        facilityCodes: List<String>? = null,
        themeCodes: List<String>? = null,
        foodCodes: List<String>? = null,
    ): Result<List<PubMapItem>>

    suspend fun getPubDetail(pubId: Long): Result<PubDetail>
}
