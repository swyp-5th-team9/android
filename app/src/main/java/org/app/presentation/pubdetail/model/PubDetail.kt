package org.app.presentation.pubdetail.model

/**
 * 펍 상세 도메인 모델.
 * 서버 명세 확정 전 임시 구조 — 필드 추가/수정 예정.
 */
data class PubDetail(
    val id: String,
    val name: String,
    /** S3 이미지 URL 목록. 비어있으면 img_moball_empty 표시 */
    val imageUrls: List<String>,
    val teams: List<KboTeam>,
    val isWishlisted: Boolean,
    val wishlistCount: Int,
    val address: String,
    val phoneNumber: String?,
    val businessHours: BusinessHours?,
    val seatCount: Int?,
    val facilities: List<FacilityItem>,
    val parkingOptions: List<ParkingItem>,
    val latitude: Double?,
    val longitude: Double?,
)

data class KboTeam(
    val id: Int,
    val shortName: String, // "LG", "KT" 등 약어
    val fullName: String, // "LG 트윈스" 등
)

data class BusinessHours(
    val openTime: String, // "17:00"
    val closeTime: String, // "02:00"
    val breakStartTime: String? = null,
    val breakEndTime: String? = null,
    val lastOrder: String? = null,
    val isOpenNow: Boolean,
    val status: BusinessStatus,
    /** 요일별 운영시간 (추후 서버 명세에 따라 확장) */
    val weeklyHours: Map<String, String> = emptyMap(),
)

enum class BusinessStatus {
    OPEN, // 영업중
    CLOSED, // 영업종료
    BREAK, // 브레이크타임
}

data class FacilityItem(
    val type: FacilityType,
    val available: Boolean = true,
)

enum class FacilityType(
    val label: String,
) {
    GROUP_SEATING("단체석"),
    WAITING_AREA("대기공간"),
    CORKAGE("콜키지 가능"),
    PRIVATE_ROOM("프라이빗룸"),
    SMOKING_AREA("흡연공간"),
    PROJECTOR("빔프로젝터"),
    SOUND_SYSTEM("음향시설"),
}

data class ParkingItem(
    val type: ParkingType,
    val description: String? = null,
)

enum class ParkingType(
    val label: String,
) {
    AVAILABLE("주차가능"),
    NEARBY("인근 유료주차장"),
    VALET("발렛파킹"),
    NONE("주차 불가"),
}
