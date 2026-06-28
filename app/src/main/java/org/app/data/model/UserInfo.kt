package org.app.data.model

data class UserInfo(
    val userId: Long,
    val nickname: String,
    val role: String,
    val onboardingCompleted: Boolean,
    val favoriteTeams: List<FavoriteTeam>,
)

data class FavoriteTeam(
    val teamId: Long,
    val teamName: String,
)
