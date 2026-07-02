package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.remote.datasource.api.MatchRemoteDataSource
import org.app.data.repository.api.MatchRepository
import org.app.presentation.schedule.model.GameSchedule
import javax.inject.Inject

class MatchRepositoryImpl
    @Inject
    constructor(
        private val matchRemoteDataSource: MatchRemoteDataSource,
    ) : MatchRepository {
        override suspend fun getMatches(
            sportType: String?,
            date: String?,
            from: String?,
            to: String?,
            teamId: Long?,
        ): Result<List<GameSchedule>> =
            suspendRunCatching {
                matchRemoteDataSource.getMatches(
                    sportType = sportType,
                    date = date,
                    from = from,
                    to = to,
                    teamId = teamId,
                )
            }
    }
