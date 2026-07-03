package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamResponse(
    @SerialName("teamId") val teamId: Long,
    @SerialName("shortName") val shortName: String = "",
    @SerialName("name") val name: String = "",
)

@Serializable
data class MatchItemResponse(
    @SerialName("matchId") val matchId: Long,
    @SerialName("sportType") val sportType: String = "KBO",
    @SerialName("matchDate") val matchDate: String,
    @SerialName("startTime") val startTime: String? = null,
    @SerialName("stadium") val stadium: String = "",
    @SerialName("status") val status: String = "SCHEDULED",
    @SerialName("homeTeam") val homeTeam: MatchTeamResponse,
    @SerialName("awayTeam") val awayTeam: MatchTeamResponse,
    @SerialName("homeScore") val homeScore: Int? = null,
    @SerialName("awayScore") val awayScore: Int? = null,
)

@Serializable
data class GetMatchesResponse(
    @SerialName("matches") val matches: List<MatchItemResponse>,
)
