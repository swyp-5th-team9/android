package org.app.data.mapper

import org.app.data.model.FavoriteTeam
import org.app.data.model.UserInfo
import org.app.data.remote.dto.FavoriteTeamResponse
import org.app.data.remote.dto.GetUserResponse

fun GetUserResponse.toUserInfo(): UserInfo =
    UserInfo(
        userId = userId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        role = role,
        onboardingCompleted = onboardingCompleted,
        favoriteTeams = favoriteTeams.map { it.toFavoriteTeam() },
    )

fun FavoriteTeamResponse.toFavoriteTeam(): FavoriteTeam =
    FavoriteTeam(
        teamId = teamId,
        teamName = teamName,
    )
