package org.app.data.repository.api

import org.app.data.model.UserInfo

interface UserRepository {
    /** POST /api/v1/users/me/onboarding */
    suspend fun postOnboarding(
        nickname: String,
        teamIds: List<Long>,
    ): Result<Unit>

    /** GET /api/v1/users/me */
    suspend fun getUser(): Result<UserInfo>

    /** PATCH /api/v1/users/me */
    suspend fun patchUser(
        nickname: String? = null,
        teamIds: List<Long>? = null,
    ): Result<Unit>

    /** DELETE /api/v1/users/me */
    suspend fun deleteUser(
        reasonCode: String,
        detail: String? = null,
    ): Result<Unit>

    /**
     * 프로필 이미지를 내부 저장소에 압축 저장한다.
     *
     * TODO 서버 프로필 이미지 업로드 API 추가 시 서버 업로드로 교체
     * @return 저장된 파일의 절대 경로
     */
    suspend fun saveLocalProfileImage(uriString: String): Result<String>

    /** 로컬에 저장된 프로필 이미지 경로. 없으면 null */
    suspend fun getLocalProfileImagePath(): Result<String?>
}
