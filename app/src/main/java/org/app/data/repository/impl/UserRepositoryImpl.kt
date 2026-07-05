package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.local.datasource.api.LocalProfileImageDataSource
import org.app.data.mapper.toUserInfo
import org.app.data.model.UserInfo
import org.app.data.remote.datasource.api.UserRemoteDataSource
import org.app.data.remote.dto.checkSuccess
import org.app.data.remote.dto.getDataOrThrow
import org.app.data.repository.api.UserRepository
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val userRemoteDataSource: UserRemoteDataSource,
        private val localProfileImageDataSource: LocalProfileImageDataSource,
    ) : UserRepository {
        override suspend fun postOnboarding(
            nickname: String,
            teamIds: List<Long>,
        ): Result<Unit> =
            suspendRunCatching {
                userRemoteDataSource.postOnboarding(nickname = nickname, teamIds = teamIds).checkSuccess()
            }

        override suspend fun getUser(): Result<UserInfo> =
            suspendRunCatching {
                userRemoteDataSource.getUser().getDataOrThrow().toUserInfo()
            }

        override suspend fun patchUser(
            nickname: String?,
            teamIds: List<Long>?,
        ): Result<Unit> =
            suspendRunCatching {
                require(nickname != null || teamIds != null) { "변경할 필드가 없습니다." }
                userRemoteDataSource.patchUser(nickname = nickname, teamIds = teamIds).checkSuccess()
            }

        override suspend fun deleteUser(
            reasonCode: String,
            detail: String?,
        ): Result<Unit> =
            suspendRunCatching {
                userRemoteDataSource.deleteUser(reasonCode = reasonCode, detail = detail).checkSuccess()
            }

        override suspend fun saveLocalProfileImage(uriString: String): Result<String> =
            suspendRunCatching {
                localProfileImageDataSource.saveProfileImage(uriString)
            }

        override suspend fun getLocalProfileImagePath(): Result<String?> =
            suspendRunCatching {
                localProfileImageDataSource.getProfileImagePath()
            }
    }
