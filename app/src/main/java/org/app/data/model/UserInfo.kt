package org.app.data.model

data class UserInfo(
    val userId: Long,
    val nickname: String,
    /** 프로필 이미지 S3 URL. 미설정 시 null */
    val profileImageUrl: String? = null,
    val role: String,
    val onboardingCompleted: Boolean,
    val favoriteTeams: List<FavoriteTeam>,
)

data class FavoriteTeam(
    val teamId: Long,
    val teamName: String,
)
