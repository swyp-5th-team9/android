package org.app.data.remote.datasource.impl

import com.moball.app.BuildConfig
import org.app.data.remote.datasource.api.UserRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.DeleteUserRequest
import org.app.data.remote.dto.FavoriteTeamResponse
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
        private val mockTeamNames = mapOf(
            1L to "LG",
            2L to "두산",
            3L to "KT",
            4L to "SSG",
            5L to "NC",
            6L to "KIA",
            7L to "롯데",
            8L to "삼성",
            9L to "한화",
            10L to "키움",
        )

        private var mockUser: GetUserResponse? = GetUserResponse(
            userId = 1L,
            nickname = "모볼매니아",
            role = "FAN",
            onboardingCompleted = false,
            favoriteTeams = emptyList(),
        )

        override suspend fun postOnboarding(
            nickname: String,
            teamIds: List<Long>,
        ): BaseResponse<Unit> {
            if (BuildConfig.USE_MOCK_SERVER) {
                mockUser = checkNotNull(mockUser).copy(
                    nickname = nickname,
                    onboardingCompleted = true,
                    favoriteTeams = teamIds.map {
                        FavoriteTeamResponse(
                            teamId = it,
                            teamName =
                                mockTeamNames[it] ?: "팀$it",
                        )
                    },
                )
                return BaseResponse(success = true, data = Unit)
            }
            return userService.postOnboarding(PostOnboardingRequest(nickname = nickname, teamIds = teamIds))
        }

        override suspend fun getUser(): BaseResponse<GetUserResponse> {
            if (BuildConfig.USE_MOCK_SERVER) {
                return BaseResponse(
                    success = true,
                    data = checkNotNull(mockUser) { "탈퇴한 사용자입니다." },
                )
            }
            return userService.getUser()
        }

        override suspend fun patchUser(
            nickname: String?,
            teamIds: List<Long>?,
        ): BaseResponse<Unit> {
            if (BuildConfig.USE_MOCK_SERVER) {
                val current = checkNotNull(mockUser) { "탈퇴한 사용자입니다." }
                mockUser = current.copy(
                    nickname = nickname ?: current.nickname,
                    favoriteTeams =
                        teamIds?.map { FavoriteTeamResponse(teamId = it, teamName = mockTeamNames[it] ?: "팀$it") }
                            ?: current.favoriteTeams,
                )
                return BaseResponse(success = true, data = Unit)
            }
            return userService.patchUser(PatchUserRequest(nickname = nickname, teamIds = teamIds))
        }

        override suspend fun deleteUser(
            reasonCode: String,
            detail: String?,
        ): BaseResponse<Unit> {
            if (BuildConfig.USE_MOCK_SERVER) {
                mockUser = null
                return BaseResponse(success = true, data = Unit)
            }
            return userService.deleteUser(DeleteUserRequest(reasonCode = reasonCode, detail = detail))
        }
    }
