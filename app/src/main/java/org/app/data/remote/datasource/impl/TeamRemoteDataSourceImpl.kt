package org.app.data.remote.datasource.impl

import org.app.data.mapper.toTeamItem
import org.app.data.model.TeamItem
import org.app.data.remote.datasource.api.TeamRemoteDataSource
import org.app.data.remote.service.TeamService
import javax.inject.Inject

class TeamRemoteDataSourceImpl
    @Inject
    constructor(
        private val teamService: TeamService,
    ) : TeamRemoteDataSource {
        override suspend fun getTeams(sportType: String?): List<TeamItem> =
            teamService
                .getTeams(sportType)
                .data
                ?.teams
                ?.map { it.toTeamItem() }
                ?: emptyList()
    }
