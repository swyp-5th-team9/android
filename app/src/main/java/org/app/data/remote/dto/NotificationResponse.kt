package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 알림 목록 조회 응답 아이템. 서버 명세(`GET /api/v1/notifications`)와 1:1 대응한다.
 *
 * 응답 `data`는 이 객체의 배열이므로 별도 래퍼 DTO 없이
 * `BaseResponse<List<NotificationResponse>>` 형태로 사용한다.
 */
@Serializable
data class NotificationResponse(
    @SerialName("notificationId")
    val notificationId: Long,
    @SerialName("matchId")
    val matchId: Long,
    @SerialName("teamIds")
    val teamIds: List<Long> = emptyList(),
    @SerialName("type")
    val type: String,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("isRead")
    val isRead: Boolean = false,
    @SerialName("deepLinkType")
    val deepLinkType: String? = null,
)
