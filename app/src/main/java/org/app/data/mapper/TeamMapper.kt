package org.app.data.mapper

import org.app.data.model.TeamItem
import org.app.data.remote.dto.TeamItemResponse

fun TeamItemResponse.toTeamItem(): TeamItem =
    TeamItem(
        teamId = teamId,
        name = name,
        shortName = shortName,
        sportType = sportType,
        homeStadium = homeStadium,
    )
