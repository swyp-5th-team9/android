package org.app.data.mapper

import org.app.data.model.Notification
import org.app.data.model.NotificationDeepLinkType
import org.app.data.model.NotificationType
import org.app.data.remote.dto.NotificationResponse
import java.time.OffsetDateTime

fun NotificationResponse.toNotification(): Notification =
    Notification(
        id = notificationId,
        matchId = matchId,
        teamIds = teamIds,
        type = NotificationType.from(type),
        title = title,
        content = content,
        createdAt = runCatching { OffsetDateTime.parse(createdAt) }.getOrNull(),
        isRead = isRead,
        deepLinkType = NotificationDeepLinkType.from(deepLinkType),
    )
