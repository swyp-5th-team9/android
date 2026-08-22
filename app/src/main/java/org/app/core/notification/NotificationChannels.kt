package org.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import com.moball.app.R

/**
 * FCM 알림 채널 정의 및 생성.
 *
 * 채널은 앱 시작 시 [org.app.core.notification.createNotificationChannels]로 1회 등록하면 되고,
 * 이미 존재하면 시스템이 무시하므로 반복 호출해도 안전하다.
 * (Android 8.0(API 26)+ 필수. minSdk 28이므로 항상 생성한다.)
 */
object MoballNotificationChannel {
    /** 기본 채널 id — AndroidManifest의 default_notification_channel_id 메타데이터와 반드시 일치. */
    const val DEFAULT_ID = "moball_default_channel"
}

/** 앱 알림 채널을 생성한다. [android.app.Application.onCreate]에서 호출. */
fun createNotificationChannels(context: Context) {
    val manager = context.getSystemService<NotificationManager>() ?: return
    val defaultChannel = NotificationChannel(
        MoballNotificationChannel.DEFAULT_ID,
        context.getString(R.string.default_notification_channel_name),
        NotificationManager.IMPORTANCE_HIGH,
    ).apply {
        description = context.getString(R.string.default_notification_channel_description)
    }
    manager.createNotificationChannel(defaultChannel)
}
