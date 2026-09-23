package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.NotificationResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationService {
    @GET("/api/v1/notifications")
    suspend fun getNotifications(): BaseResponse<List<NotificationResponse>>

    @DELETE("/api/v1/notifications/{notificationId}")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: Long,
    ): BaseResponse<Unit>

    @PATCH("/api/v1/notifications/{notificationId}/read")
    suspend fun readNotification(
        @Path("notificationId") notificationId: Long,
    ): BaseResponse<Unit>
}
