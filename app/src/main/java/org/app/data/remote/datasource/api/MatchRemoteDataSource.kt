package org.app.data.remote.datasource.api

import org.app.presentation.schedule.model.GameSchedule

interface MatchRemoteDataSource {
    suspend fun getMatches(
        sportType: String? = null,
        date: String? = null,
        from: String? = null,
        to: String? = null,
        teamId: Long? = null,
    ): List<GameSchedule>
}
