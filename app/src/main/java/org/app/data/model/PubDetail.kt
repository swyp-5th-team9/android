package org.app.data.model

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 펍 상세 도메인 모델 — 서버 명세 V3 기준.
 */
data class PubDetail(
    val pubId: Long,
    val name: String,
    val address: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String?,
    val status: PubStatus,
    val capacityRange: String?,
    val groupSeatMaxPeople: Int?,
    val favoriteCount: Int,
    val description: String?,
    /** S3 이미지 URL 목록 (displayOrder 오름차순). 비어있으면 빈 화면 처리 */
    val imageUrls: List<String>,
    val teams: List<KboTeam>,
    val facilityCodes: List<String>,
    val styleCodes: List<String>,
    val themeCodes: List<String>,
    val foodCodes: List<String>,
    val businessHours: List<BusinessHour>,
    val menus: List<PubMenu>,
    /** FavoriteRepository에서 별도로 채워지는 필드 */
    val isWishlisted: Boolean = false,
)

enum class PubStatus(
    val label: String,
) {
    OPEN("영업중"),
    CLOSED("영업종료"),
    TEMP_CLOSED("임시휴업"),
    MATCHING("상영중"),
    ;

    companion object {
        fun from(value: String): PubStatus = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CLOSED
    }
}

/**
 * 표시용 영업상태 라벨.
 *
 * 서버 [serverStatus]가 실제 영업시간/휴무를 반영하지 못하는 경우가 있어(마감 시간이 지났거나
 * 휴무일인데 "영업중"으로 내려오는 등), [businessHours](KST 기준)와 현재 시각 [now]를 근거로 보정한다.
 * - 오늘이 휴무일 → "휴무"
 * - 서버가 임시휴업/상영중을 명시 → 그 값 우선
 * - 그 외 → 현재 시각이 영업시간 이내면 "영업중", 아니면 "영업종료"
 *
 * businessHours가 비어있으면(상세 로드 전) 서버 status를 그대로 사용한다.
 */
fun pubStatusLabel(
    serverStatus: PubStatus,
    businessHours: List<BusinessHour>,
    now: LocalDateTime,
): String {
    if (businessHours.isEmpty()) return serverStatus.label

    val today = businessHours.firstOrNull { it.dayOfWeek == now.dayOfWeek.value }
    if (today?.isClosed == true) return "휴무"
    if (serverStatus == PubStatus.TEMP_CLOSED || serverStatus == PubStatus.MATCHING) return serverStatus.label

    return if (isOpenNow(businessHours, now)) PubStatus.OPEN.label else PubStatus.CLOSED.label
}

/**
 * 현재 시각 [now]가 영업시간 이내인지 판정. 자정을 넘겨 마감하는 경우(예: 17:00~02:00)와
 * 어제 오픈해서 오늘 새벽까지 이어지는 영업을 함께 고려한다.
 */
private fun isOpenNow(businessHours: List<BusinessHour>, now: LocalDateTime): Boolean {
    val nowTime = now.toLocalTime()
    val todayIso = now.dayOfWeek.value

    val today = businessHours.firstOrNull { it.dayOfWeek == todayIso }
    if (today != null && !today.isClosed && today.openTime != null && today.closeTime != null) {
        val open = today.openTime
        val close = today.closeTime
        if (close > open) {
            // 같은 날 안에서 영업 (예: 09:00~22:00)
            if (!nowTime.isBefore(open) && nowTime.isBefore(close)) return true
        } else {
            // 자정을 넘겨 마감 (예: 17:00~02:00) → 오늘 open 이후는 영업 중
            if (!nowTime.isBefore(open)) return true
        }
    }

    // 어제 오픈해서 자정을 넘겨 오늘 새벽까지 영업하는 경우 (예: 어제 20:00~02:00, 지금 01:00)
    val yesterdayIso = if (todayIso == 1) 7 else todayIso - 1
    val yesterday = businessHours.firstOrNull { it.dayOfWeek == yesterdayIso }
    if (yesterday != null && !yesterday.isClosed && yesterday.openTime != null && yesterday.closeTime != null) {
        if (yesterday.closeTime <= yesterday.openTime && nowTime.isBefore(yesterday.closeTime)) return true
    }

    return false
}

data class KboTeam(
    val teamId: Long,
    val shortName: String,
    val name: String?,
)

/** ISO-8601 기준 요일별 영업시간 (1=월, 7=일) */
data class BusinessHour(
    val dayOfWeek: Int,
    val openTime: LocalTime?,
    val closeTime: LocalTime?,
    val isClosed: Boolean,
) {
    val dayLabel: String
        get() = when (dayOfWeek) {
            1 -> "월"
            2 -> "화"
            3 -> "수"
            4 -> "목"
            5 -> "금"
            6 -> "토"
            7 -> "일"
            else -> "-"
        }
}

data class PubMenu(
    val menuId: Long,
    val name: String,
    val category: String,
    /** null = 시가 */
    val price: Int?,
    val displayOrder: Int,
)
