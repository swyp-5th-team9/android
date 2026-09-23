package org.app.data.model

import java.time.OffsetDateTime

/**
 * 알림 도메인 모델. 앱 내부에서 사용하는 형태로 [org.app.data.remote.dto.NotificationResponse]를 변환한 결과.
 *
 * - [type] / [deepLinkType]는 서버 문자열을 enum으로 변환하며, 정의되지 않은 값은 [NotificationType.UNKNOWN] /
 *   [NotificationDeepLinkType.UNKNOWN]으로 처리한다(명세: 미정의 값은 클라이언트에서 무시/기본 처리).
 * - [teamIds]는 경기 홈팀·원정팀 ID. 알림 클릭 후 펍 조회 시 다중 구단 OR 필터로 사용한다.
 */
data class Notification(
    val id: Long,
    val matchId: Long,
    val teamIds: List<Long>,
    val type: NotificationType,
    val title: String,
    val content: String,
    val createdAt: OffsetDateTime?,
    val isRead: Boolean,
    val deepLinkType: NotificationDeepLinkType,
)

/** 알림 유형. 현재는 경기 일정 리마인드만 존재한다. */
enum class NotificationType {
    GAME_REMINDER,
    UNKNOWN,
    ;

    companion object {
        fun from(raw: String?): NotificationType = entries.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}

/** 알림 클릭 시 이동할 화면 유형. */
enum class NotificationDeepLinkType {
    /** 오늘 요일 필터가 적용된 펍 리스트 */
    TODAY_PUBS,

    /** 내일 요일 필터가 적용된 펍 리스트 */
    TOMORROW_GAME,

    /** 내 위치 기반 가까운 펍 지도/리스트 */
    NEARBY_PUBS,

    /** 미정의 값 (무시/기본 처리) */
    UNKNOWN,
    ;

    companion object {
        fun from(raw: String?): NotificationDeepLinkType = entries.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}
