package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.GetUserResponse

interface UserRemoteDataSource {
    suspend fun postOnboarding(
        nickname: String,
        teamIds: List<Long>,
    ): BaseResponse<Unit>

    suspend fun getUser(): BaseResponse<GetUserResponse>

    suspend fun patchUser(
        nickname: String?,
        teamIds: List<Long>?,
    ): BaseResponse<Unit>

    suspend fun deleteUser(
        reasonCode: String,
        detail: String?,
    ): BaseResponse<Unit>
}
