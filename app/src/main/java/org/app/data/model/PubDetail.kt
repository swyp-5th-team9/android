package org.app.data.model

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
