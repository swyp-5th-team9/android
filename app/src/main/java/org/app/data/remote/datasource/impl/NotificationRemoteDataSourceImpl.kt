package org.app.data.remote.datasource.impl

import org.app.data.remote.datasource.api.NotificationRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.NotificationResponse
import org.app.data.remote.service.NotificationService
import javax.inject.Inject

class NotificationRemoteDataSourceImpl
    @Inject
    constructor(
        private val notificationService: NotificationService,
    ) : NotificationRemoteDataSource {
        override suspend fun getNotifications(): BaseResponse<List<NotificationResponse>> =
            notificationService.getNotifications()

        override suspend fun deleteNotification(notificationId: Long): BaseResponse<Unit> =
            notificationService.deleteNotification(notificationId)

        override suspend fun readNotification(notificationId: Long): BaseResponse<Unit> =
            notificationService.readNotification(notificationId)
    }
