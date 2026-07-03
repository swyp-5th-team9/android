package org.app.presentation.home.model

/**
 * 지도에 표시되는 펍 마커
 * @param pubId     펍 고유 ID
 * @param name      펍 이름
 * @param latitude  위도
 * @param longitude 경도
 * @param type      마커 유형 (경기 상영 중 / 내가 찜한)
 * @param teamId    연관 구단 ID (type=MATCH 일 때 사용)
 * @param isFavorite 내가 찜한 여부
 */
data class PubMarker(
    val pubId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: PubMarkerType,
    val teamId: Int? = null,
    val isFavorite: Boolean = false,
)

/** 지도 핀 종류 */
enum class PubMarkerType {
    /** 구단 경기 상영 중인 펍 — ic_pin (야구공 아이콘) */
    MATCH,

    /** 내가 찜한 펍 — ic_pub_favorite (별 아이콘) */
    FAVORITE,
}

/**
 * 줌아웃 시 여러 마커를 묶어 표시하는 클러스터 마커
 * @param latitude  클러스터 중심 위도
 * @param longitude 클러스터 중심 경도
 * @param count     클러스터 내 펍 수
 */
data class PubCluster(
    val latitude: Double,
    val longitude: Double,
    val count: Int,
)

/**
 * 검색 결과 항목
 * @param pubId   펍 고유 ID
 * @param name    펍 이름
 * @param address 주소
 */
data class PubSearchResult(
    val pubId: String,
    val name: String,
    val address: String,
)

/**
 * 홈 화면 필터 상태
 * @param selectedTeamIds   선택된 구단 ID 목록
 * @param selectedTeamNames 선택된 구단 이름 목록 (칩 라벨 표시용)
 * @param selectedRegions   선택된 지역 이름 목록
 */
data class HomeFilter(
    val selectedTeamIds: List<Long> = emptyList(),
    val selectedTeamNames: List<String> = emptyList(),
    val selectedRegions: List<String> = emptyList(),
    val openNow: Boolean? = null,
    val businessDay: String? = null,
    val facilityCodes: List<String>? = null,
    val themeCodes: List<String>? = null,
    val foodCodes: List<String>? = null,
) {
    val isTeamFilterActive: Boolean get() = selectedTeamIds.isNotEmpty()
    val isRegionFilterActive: Boolean get() = selectedRegions.isNotEmpty()
    val teamChipLabel: String
        get() = when {
            selectedTeamNames.isEmpty() -> ""
            selectedTeamNames.size == 1 -> selectedTeamNames.first()
            else -> "${selectedTeamNames.first()} 외 ${selectedTeamNames.size - 1}개"
        }

    val regionChipLabel: String
        get() {
            val labels = selectedRegions.mapNotNull { REGION_DISPLAY_LABELS[it] }
            return when {
                labels.isEmpty() -> "지역"
                labels.size == 1 -> labels.first()
                else -> "${labels.first()} 외 ${labels.size - 1}개"
            }
        }
}

/**
 * 펍 필터 전체화면에서 사용하는 섹션 모델 (서버 명세 확정 전 클라이언트 정의)
 */
data class PubFilterSection(
    val sectionId: String,
    val title: String,
    val options: List<PubFilterOption>,
)

data class PubFilterOption(
    val id: String,
    val label: String,
)

/**
 * 백엔드 region 코드 → 한글 표시 라벨
 * 칩 라벨 표시 / RegionMapper 대체 키로 사용
 */
val REGION_DISPLAY_LABELS: Map<String, String> = mapOf(
    "SEOUL_ALL" to "서울 전체",
    "GANGNAM" to "강남/서초",
    "JAMSIL" to "잠실/잠실새내",
    "SONGPA" to "송파",
    "GANGDONG" to "강동/천호",
    "SEONGDONG" to "성수/왕십리",
    "JUNGNANG" to "중랑",
    "JONGNO" to "종로",
    "JUNGGU" to "중구",
    "HONGDAE_HAPJEONG" to "홍대/합정",
    "SANGAM_MANGWON" to "상암/망원",
    "MAPO" to "마포",
    "EUNPYEONG" to "은평/서대문",
    "YEONGDEUNGPO" to "영등포/여의도",
    "GANGSEO" to "마곡/강서",
    "DONGJAK" to "동작",
    "NOWON" to "노원/강북",
    "DOBONG" to "도봉/성북",
    "GURO" to "구로",
    "GWANAK" to "관악",
)

object RegionMapper {
    /** 백엔드 region 코드 → 대표 위경도 (지도 카메라 이동용) */
    private val regionCoords = mapOf(
        "SEOUL_ALL" to (37.5665 to 126.9780),
        "GANGNAM" to (37.4981 to 127.0276),
        "JAMSIL" to (37.5145 to 127.1058),
        "SONGPA" to (37.5049 to 127.1154),
        "GANGDONG" to (37.5301 to 127.1238),
        "SEONGDONG" to (37.5408 to 127.0691),
        "JUNGNANG" to (37.6063 to 127.0931),
        "JONGNO" to (37.5730 to 126.9794),
        "JUNGGU" to (37.5635 to 126.9975),
        "HONGDAE_HAPJEONG" to (37.5567 to 126.9235),
        "SANGAM_MANGWON" to (37.5605 to 126.9097),
        "MAPO" to (37.5538 to 126.9510),
        "EUNPYEONG" to (37.6027 to 126.9289),
        "YEONGDEUNGPO" to (37.5264 to 126.8962),
        "GANGSEO" to (37.5518 to 126.8495),
        "DONGJAK" to (37.5024 to 126.9393),
        "NOWON" to (37.6539 to 127.0569),
        "DOBONG" to (37.6543 to 127.0472),
        "GURO" to (37.4954 to 126.8874),
        "GWANAK" to (37.4784 to 126.9516),
    )

    fun getLatLng(regionCode: String): Pair<Double, Double>? = regionCoords[regionCode]
}
