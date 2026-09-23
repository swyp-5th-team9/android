package org.app.data.repository.api

import org.app.data.model.Notification

interface NotificationRepository {
    /** 알림 목록 조회 (최신순) */
    suspend fun getNotifications(): Result<List<Notification>>

    /** 알림 삭제 (Hard Delete) */
    suspend fun deleteNotification(notificationId: Long): Result<Unit>

    /** 알림 읽음 처리 (멱등: 이미 읽음이어도 성공) */
    suspend fun readNotification(notificationId: Long): Result<Unit>
}
