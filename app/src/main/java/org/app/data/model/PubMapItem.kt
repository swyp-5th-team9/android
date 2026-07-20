package org.app.data.model

import java.time.LocalTime

data class PubMapItem(
    val pubId: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val status: PubStatus,
    val favoriteCount: Int,
    val imageUrls: List<String> = emptyList(),
    val supportedTeams: List<PubTeam> = emptyList(),
    val facilityCodes: List<String> = emptyList(),
    val styleCodes: List<String> = emptyList(),
    val themeCodes: List<String> = emptyList(),
    val foodCodes: List<String> = emptyList(),
    val openTime: LocalTime? = null,
    val closeTime: LocalTime? = null,
    val groupSeatMaxPeople: Int? = null,
    val capacityRange: String? = null,
)
