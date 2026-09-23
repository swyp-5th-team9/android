package org.app.core.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.moball.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import org.app.presentation.main.MainActivity
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * FCM data 메시지를 수신했을 때 시스템 알림을 만들어 표시한다.
 *
 * 서버가 notification 페이로드로 보내면(=앱이 백그라운드) FCM SDK가 자동으로 표시하지만,
 * 앱이 포그라운드이거나 data-only 메시지인 경우엔 [MoballMessagingService]에서 이 클래스로 직접 표시한다.
 *
 * 탭 동작: deepLinkType/teamIds가 있으면 Intent extras로 실어 [MainActivity]가 딥링크 라우팅한다.
 */
@Singleton
class MoballNotifier
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun show(
            title: String?,
            body: String?,
            deepLinkType: String? = null,
            teamIds: String? = null,
        ) {
            // Android 13+ 에서 권한이 없으면 조용히 무시 (권한 요청은 UI 레이어 담당)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                NotificationDeepLink.putExtras(this, deepLinkType, teamIds)
            }
            // 알림마다 고유 id를 써서 서로 다른 딥링크 extras가 덮어써지지 않도록 한다.
            val notificationId = Random.nextInt()
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val notification = NotificationCompat
                .Builder(context, MoballNotificationChannel.DEFAULT_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.notification_accent))
                .setContentTitle(title ?: context.getString(R.string.app_name))
                .apply {
                    body?.let {
                        setContentText(it)
                        setStyle(NotificationCompat.BigTextStyle().bigText(it))
                    }
                }.setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
