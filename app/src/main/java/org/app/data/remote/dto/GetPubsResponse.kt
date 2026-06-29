package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPubsResponse(
    @SerialName("content") val content: List<PubListItemResponse>,
    @SerialName("page") val page: Int,
    @SerialName("size") val size: Int,
    @SerialName("totalElements") val totalElements: Int,
    @SerialName("totalPages") val totalPages: Int,
)

@Serializable
data class PubListItemResponse(
    @SerialName("pubId") val pubId: Long,
    @SerialName("name") val name: String,
    @SerialName("region") val region: String,
    @SerialName("address") val address: String,
    @SerialName("thumbnailUrl") val thumbnailUrl: String?,
    @SerialName("favoriteCount") val favoriteCount: Int,
    @SerialName("businessStatus") val businessStatus: String,
    @SerialName("supportedTeams") val supportedTeams: List<SupportedTeamResponse>,
    @SerialName("facilityCodes") val facilityCodes: List<String>,
    @SerialName("styleCodes") val styleCodes: List<String>,
    @SerialName("themeCodes") val themeCodes: List<String>,
    @SerialName("foodCodes") val foodCodes: List<String>,
)

@Serializable
data class SupportedTeamResponse(
    @SerialName("teamId") val teamId: Long,
    @SerialName("shortName") val shortName: String,
    @SerialName("name") val name: String? = null,
)
