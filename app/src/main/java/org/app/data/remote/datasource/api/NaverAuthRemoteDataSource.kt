package org.app.data.remote.datasource.api

interface NaverAuthRemoteDataSource {
    suspend fun logoutNaver(): Result<Unit>

    suspend fun withdrawNaver(): Result<Unit>
}
