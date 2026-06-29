package org.app.data.repository.api

import org.app.data.model.TeamItem

interface TeamRepository {
    suspend fun getTeams(sportType: String? = null): Result<List<TeamItem>>
}
