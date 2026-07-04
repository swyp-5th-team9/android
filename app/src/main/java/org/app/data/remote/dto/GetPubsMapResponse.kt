package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPubsMapResponse(
    @SerialName("pubs") val pubs: List<PubMapItemResponse>,
)

@Serializable
data class PubMapItemResponse(
    @SerialName("pubId") val pubId: Long,
    @SerialName("name") val name: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("status") val status: String,
    @SerialName("favoriteCount") val favoriteCount: Int,
    @SerialName("imageUrls") val imageUrls: List<String>? = null,
    @SerialName("supportedTeams") val supportedTeams: List<String>? = null,
    @SerialName("facilityCodes") val facilityCodes: List<String>? = null,
    @SerialName("openTime") val openTime: String? = null,
    @SerialName("closeTime") val closeTime: String? = null,
    @SerialName("groupSeatMaxPeople") val groupSeatMaxPeople: Int? = null,
    @SerialName("capacityRange") val capacityRange: String? = null,
)
