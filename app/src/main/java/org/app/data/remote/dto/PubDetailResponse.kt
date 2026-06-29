package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PubDetailResponse(
    @SerialName("pubId") val pubId: Long,
    @SerialName("name") val name: String,
    @SerialName("address") val address: String,
    @SerialName("region") val region: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("phone") val phone: String?,
    @SerialName("status") val status: String,
    @SerialName("capacityRange") val capacityRange: String?,
    @SerialName("groupSeatMaxPeople") val groupSeatMaxPeople: Int?,
    @SerialName("favoriteCount") val favoriteCount: Int,
    @SerialName("description") val description: String?,
    @SerialName("images") val images: List<PubImageResponse>,
    @SerialName("supportedTeams") val supportedTeams: List<SupportedTeamResponse>,
    @SerialName("facilityCodes") val facilityCodes: List<String>,
    @SerialName("styleCodes") val styleCodes: List<String>,
    @SerialName("themeCodes") val themeCodes: List<String>,
    @SerialName("foodCodes") val foodCodes: List<String>,
    @SerialName("businessHours") val businessHours: List<BusinessHourResponse>,
    @SerialName("menus") val menus: List<MenuResponse>,
)

@Serializable
data class PubImageResponse(
    @SerialName("imageId") val imageId: Long,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("displayOrder") val displayOrder: Int,
)

@Serializable
data class BusinessHourResponse(
    @SerialName("dayOfWeek") val dayOfWeek: Int,
    @SerialName("openTime") val openTime: String?,
    @SerialName("closeTime") val closeTime: String?,
    @SerialName("isClosed") val isClosed: Boolean,
)

@Serializable
data class MenuResponse(
    @SerialName("menuId") val menuId: Long,
    @SerialName("name") val name: String,
    @SerialName("category") val category: String,
    @SerialName("price") val price: Int?,
    @SerialName("displayOrder") val displayOrder: Int,
)
