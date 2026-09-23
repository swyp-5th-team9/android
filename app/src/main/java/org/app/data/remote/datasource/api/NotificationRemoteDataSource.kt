package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.NotificationResponse

interface NotificationRemoteDataSource {
    suspend fun getNotifications(): BaseResponse<List<NotificationResponse>>

    suspend fun deleteNotification(notificationId: Long): BaseResponse<Unit>

    suspend fun readNotification(notificationId: Long): BaseResponse<Unit>
}
