package org.app.core.notification

import android.content.Intent
import org.app.data.model.NotificationDeepLinkType
import java.time.LocalDate
import java.time.ZoneId

/**
 * 알림 딥링크 공용 헬퍼.
 *
 * - FCM data 페이로드 / Intent extras 에서 딥링크 정보를 파싱한다.
 * - deepLinkType → 홈 펍 필터(요일)로 변환하는 규칙을 한곳에서 관리한다.
 *   (인앱 알림 카드 클릭과 FCM 푸시 탭이 동일 규칙을 공유)
 */
object NotificationDeepLink {
    const val EXTRA_DEEP_LINK_TYPE = "deepLinkType"
    const val EXTRA_TEAM_IDS = "teamIds"

    private val KST_ZONE = ZoneId.of("Asia/Seoul")

    // 서버 businessDay 요일 코드 (월~일). DayOfWeek.value(1=월..7=일) - 1 인덱스.
    private val WEEKDAY_CODES = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    /** 알림 딥링크 요청. [businessDay]는 오늘/내일 요일 서버코드로 파생된다. */
    data class Request(
        val deepLinkType: NotificationDeepLinkType,
        val teamIds: List<Long>,
    ) {
        val businessDay: String?
            get() = when (deepLinkType) {
                // 당일 경기 관련 → 오늘 요일
                NotificationDeepLinkType.TODAY_PUBS,
                NotificationDeepLinkType.NEARBY_PUBS,
                -> weekdayCodeOf(LocalDate.now(KST_ZONE))
                // 내일 경기 관련 → 내일 요일
                NotificationDeepLinkType.TOMORROW_GAME -> weekdayCodeOf(LocalDate.now(KST_ZONE).plusDays(1))
                NotificationDeepLinkType.UNKNOWN -> null
            }
    }

    /** deepLinkType/teamIds로 [Request] 생성. 미정의 타입이면 null. */
    fun of(
        deepLinkType: NotificationDeepLinkType,
        teamIds: List<Long>,
    ): Request? = if (deepLinkType == NotificationDeepLinkType.UNKNOWN) null else Request(deepLinkType, teamIds)

    /** FCM data 페이로드에서 파싱. deepLinkType이 없거나 미정의면 null. */
    fun fromData(data: Map<String, String?>): Request? {
        val type = NotificationDeepLinkType.from(data[EXTRA_DEEP_LINK_TYPE])
        return of(type, parseTeamIds(data[EXTRA_TEAM_IDS]))
    }

    /** 알림 탭으로 실행된 Intent extras에서 파싱. */
    fun fromIntent(intent: Intent?): Request? {
        intent ?: return null
        val type = intent.getStringExtra(EXTRA_DEEP_LINK_TYPE) ?: return null
        return fromData(
            mapOf(
                EXTRA_DEEP_LINK_TYPE to type,
                EXTRA_TEAM_IDS to intent.getStringExtra(EXTRA_TEAM_IDS),
            ),
        )
    }

    /** PendingIntent용 Intent에 딥링크 extras를 싣는다. */
    fun putExtras(
        intent: Intent,
        deepLinkType: String?,
        teamIds: String?,
    ) {
        deepLinkType?.let { intent.putExtra(EXTRA_DEEP_LINK_TYPE, it) }
        teamIds?.let { intent.putExtra(EXTRA_TEAM_IDS, it) }
    }

    /** 처리 완료 후 재진입(회전 등) 시 재실행을 막기 위해 extras 제거. */
    fun clearExtras(intent: Intent?) {
        intent ?: return
        intent.removeExtra(EXTRA_DEEP_LINK_TYPE)
        intent.removeExtra(EXTRA_TEAM_IDS)
    }

    private fun weekdayCodeOf(date: LocalDate): String = WEEKDAY_CODES[date.dayOfWeek.value - 1]

    // "1,2" → [1, 2]
    private fun parseTeamIds(raw: String?): List<Long> =
        raw?.split(",")?.mapNotNull { it.trim().toLongOrNull() } ?: emptyList()
}
