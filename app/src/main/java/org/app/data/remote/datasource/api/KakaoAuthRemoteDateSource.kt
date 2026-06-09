package org.app.data.remote.datasource.api

interface KakaoAuthDataSource {
    suspend fun logoutKakao(): Result<Unit>

    suspend fun withdrawKakao(): Result<Unit>
}
