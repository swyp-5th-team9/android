package org.app.data.model

data class PubMapItem(
    val pubId: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val favoriteCount: Int,
    val imageUrls: List<String> = emptyList(),
    val supportedTeams: List<String> = emptyList(),
    val facilityCodes: List<String> = emptyList(),
    val openTime: String? = null,
    val closeTime: String? = null,
    val groupSeatMaxPeople: Int? = null,
    val capacityRange: String? = null,
)
