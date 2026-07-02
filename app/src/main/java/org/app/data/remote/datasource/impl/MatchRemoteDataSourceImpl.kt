package org.app.data.remote.datasource.impl

import org.app.data.mapper.toGameSchedule
import org.app.data.remote.datasource.api.MatchRemoteDataSource
import org.app.data.remote.service.MatchService
import org.app.presentation.schedule.model.GameSchedule
import javax.inject.Inject

class MatchRemoteDataSourceImpl
    @Inject
    constructor(
        private val matchService: MatchService,
    ) : MatchRemoteDataSource {
        override suspend fun getMatches(
            sportType: String?,
            date: String?,
            from: String?,
            to: String?,
            teamId: Long?,
        ): List<GameSchedule> =
            matchService
                .getMatches(
                    sportType = sportType,
                    date = date,
                    from = from,
                    to = to,
                    teamId = teamId,
                ).data
                ?.matches
                ?.map { it.toGameSchedule() }
                ?: emptyList()
    }
