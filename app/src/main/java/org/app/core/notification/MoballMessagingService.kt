package org.app.core.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * FCM 메시지 수신 서비스.
 *
 * - [onNewToken]: 토큰이 새로 발급/갱신되면 서버에 재등록.
 * - [onMessageReceived]: data 메시지 또는 포그라운드 notification 메시지 수신 시 알림 표시.
 *   (백그라운드 + notification 페이로드는 FCM SDK가 자동 표시하므로 여기로 오지 않는다.)
 */
@AndroidEntryPoint
class MoballMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var notifier: MoballNotifier

    @Inject
    lateinit var tokenRegistrar: FcmTokenRegistrar

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM onNewToken")
        tokenRegistrar.register(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // notification 페이로드 우선, 없으면 data 페이로드의 title/body 사용
        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]
        Timber.d("FCM onMessageReceived: title=%s", title)
        notifier.show(title = title, body = body)
    }
}
