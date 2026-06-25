package org.app.data.repository.impl

import org.app.data.repository.api.UserRepository
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        // TODO: UserRemoteDataSource 주입 (API 구현 후)
    ) : UserRepository {
        override suspend fun postOnboarding(
            nickname: String,
            teamIds: List<Int>,
        ): Result<Unit> {
            // TODO: API 연결
            // POST /api/v1/users/me/onboarding
            // body: { nickname: nickname, teamIds: teamIds }
            return Result.success(Unit)
        }
    }
