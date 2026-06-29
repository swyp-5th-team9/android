package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamItemResponse(
    @SerialName("teamId") val teamId: Long,
    @SerialName("name") val name: String,
    @SerialName("shortName") val shortName: String,
    @SerialName("sportType") val sportType: String,
    @SerialName("homeStadium") val homeStadium: String? = null,
)

@Serializable
data class GetTeamsResponse(
    @SerialName("teams") val teams: List<TeamItemResponse>,
)
