package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponse(
    @SerialName("userId")
    val userId: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerialName("role")
    val role: String,
    @SerialName("onboardingCompleted")
    val onboardingCompleted: Boolean,
    @SerialName("favoriteTeams")
    val favoriteTeams: List<FavoriteTeamResponse>,
)

@Serializable
data class FavoriteTeamResponse(
    @SerialName("teamId")
    val teamId: Long,
    @SerialName("teamName")
    val teamName: String,
)
