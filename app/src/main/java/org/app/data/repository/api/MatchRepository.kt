package org.app.data.repository.api

import org.app.presentation.schedule.model.GameSchedule

interface MatchRepository {
    suspend fun getMatches(
        sportType: String? = null,
        date: String? = null,
        from: String? = null,
        to: String? = null,
        teamId: Long? = null,
    ): Result<List<GameSchedule>>
}
