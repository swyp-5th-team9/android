package org.app.data.remote.datasource.impl

import org.app.data.remote.datasource.api.UserRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.DeleteUserRequest
import org.app.data.remote.dto.GetUserResponse
import org.app.data.remote.dto.PatchUserRequest
import org.app.data.remote.dto.PostOnboardingRequest
import org.app.data.remote.service.UserService
import javax.inject.Inject

class UserRemoteDataSourceImpl
    @Inject
    constructor(
        private val userService: UserService,
    ) : UserRemoteDataSource {
        override suspend fun postOnboarding(
            nickname: String,
            teamIds: List<Long>,
        ): BaseResponse<Unit> =
            userService.postOnboarding(PostOnboardingRequest(nickname = nickname, teamIds = teamIds))

        override suspend fun getUser(): BaseResponse<GetUserResponse> = userService.getUser()

        override suspend fun patchUser(
            nickname: String?,
            teamIds: List<Long>?,
        ): BaseResponse<Unit> = userService.patchUser(PatchUserRequest(nickname = nickname, teamIds = teamIds))

        override suspend fun deleteUser(
            reasonCode: String,
            detail: String?,
        ): BaseResponse<Unit> = userService.deleteUser(DeleteUserRequest(reasonCode = reasonCode, detail = detail))
    }
