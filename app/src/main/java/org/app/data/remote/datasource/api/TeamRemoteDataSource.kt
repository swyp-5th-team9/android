package org.app.data.remote.datasource.api

import org.app.data.model.TeamItem

interface TeamRemoteDataSource {
    suspend fun getTeams(sportType: String? = null): List<TeamItem>
}
