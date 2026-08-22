package org.app.core.notification

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.app.data.repository.api.UserRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FCM 디바이스 토큰을 서버에 등록/갱신하는 진입점.
 *
 * - [syncToken]: 현재 단말의 FCM 토큰을 조회해 서버에 등록. 앱 시작(또는 로그인 후)에 호출.
 * - [register]: [MoballMessagingService.onNewToken]에서 갱신된 토큰을 전달받아 등록.
 *
 * 실제 서버 등록은 [UserRepository.registerFcmToken]에 위임하며, 백엔드 준비 전까지는
 * 그쪽이 no-op(TODO)이라 앱 흐름에는 영향이 없다.
 */
@Singleton
class FcmTokenRegistrar
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        // 앱 생명주기 동안 유지되는 등록 전용 스코프 (fire-and-forget)
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        /** 현재 FCM 토큰을 조회해 서버에 등록한다. */
        fun syncToken() {
            FirebaseMessaging
                .getInstance()
                .token
                .addOnSuccessListener { token -> register(token) }
                .addOnFailureListener { Timber.w(it, "FCM 토큰 조회 실패") }
        }

        /** 토큰(신규/갱신)을 서버에 등록한다. */
        fun register(token: String) {
            scope.launch {
                userRepository
                    .registerFcmToken(token)
                    .onFailure { Timber.w(it, "FCM 토큰 서버 등록 실패") }
            }
        }
    }
