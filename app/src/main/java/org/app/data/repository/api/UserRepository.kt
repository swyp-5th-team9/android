package org.app.data.repository.api

interface UserRepository {
    /**
     * 온보딩 완료 처리
     * POST /api/v1/users/me/onboarding
     *
     * @param nickname  닉네임 (최대 20자, 필수)
     * @param teamIds   선호 구단 ID 목록 (최대 3개, 선택)
     */
    suspend fun postOnboarding(
        nickname: String,
        teamIds: List<Int>,
    ): Result<Unit>
}
