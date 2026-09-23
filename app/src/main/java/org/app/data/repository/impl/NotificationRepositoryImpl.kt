package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.mapper.toNotification
import org.app.data.model.Notification
import org.app.data.remote.datasource.api.NotificationRemoteDataSource
import org.app.data.remote.dto.checkSuccess
import org.app.data.remote.dto.getDataOrThrow
import org.app.data.repository.api.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl
    @Inject
    constructor(
        private val notificationRemoteDataSource: NotificationRemoteDataSource,
    ) : NotificationRepository {
        override suspend fun getNotifications(): Result<List<Notification>> =
            suspendRunCatching {
                notificationRemoteDataSource
                    .getNotifications()
                    .getDataOrThrow()
                    .map { it.toNotification() }
            }

        override suspend fun deleteNotification(notificationId: Long): Result<Unit> =
            suspendRunCatching {
                notificationRemoteDataSource.deleteNotification(notificationId).checkSuccess()
            }

        override suspend fun readNotification(notificationId: Long): Result<Unit> =
            suspendRunCatching {
                notificationRemoteDataSource.readNotification(notificationId).checkSuccess()
            }
    }
