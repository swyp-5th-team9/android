package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.model.TeamItem
import org.app.data.remote.datasource.api.TeamRemoteDataSource
import org.app.data.repository.api.TeamRepository
import javax.inject.Inject

class TeamRepositoryImpl
    @Inject
    constructor(
        private val teamRemoteDataSource: TeamRemoteDataSource,
    ) : TeamRepository {
        override suspend fun getTeams(sportType: String?): Result<List<TeamItem>> =
            suspendRunCatching { teamRemoteDataSource.getTeams(sportType) }
    }
