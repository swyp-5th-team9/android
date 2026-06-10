package org.app.data.remote.datasource.api

interface KakaoAuthRemoteDataSource {
    suspend fun logoutKakao(): Result<Unit>

    suspend fun withdrawKakao(): Result<Unit>
}
