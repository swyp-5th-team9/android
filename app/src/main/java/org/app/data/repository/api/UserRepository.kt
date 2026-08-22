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

    /**
     * PATCH /api/v1/users/me (multipart)
     *
     * @param profileImageUri 갤러리 URI — 전달 시 압축 후 서버(S3)에 업로드
     */
    suspend fun patchUser(
        nickname: String? = null,
        teamIds: List<Long>? = null,
        profileImageUri: String? = null,
    ): Result<Unit>

    /** DELETE /api/v1/users/me */
    suspend fun deleteUser(
        reasonCode: String,
        detail: String? = null,
    ): Result<Unit>

    /**
     * FCM 디바이스 토큰을 서버에 등록/갱신한다.
     *
     * TODO(#알림): 백엔드 엔드포인트가 준비되면 [UserRepositoryImpl.registerFcmToken]의
     *  실제 호출 주석을 해제한다. 그 전까지는 no-op(성공 처리)으로 앱 흐름에 영향 없음.
     */
    suspend fun registerFcmToken(token: String): Result<Unit>
}
