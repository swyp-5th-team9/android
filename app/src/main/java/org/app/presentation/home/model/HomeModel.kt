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
        get() = when {
            selectedRegions.isEmpty() -> "지역"
            selectedRegions.size == 1 -> selectedRegions.first()
            else -> "${selectedRegions.first()} 외 ${selectedRegions.size - 1}개"
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

object RegionMapper {
    /** 지역 이름별 대표 위경도 (지도로 이동/줌 조절용) */
    private val regionCoords = mapOf(
        "서울 전체" to (37.5665 to 126.9780),
        "강남/역삼" to (37.4981 to 127.0276),
        "서초/방배/반포" to (37.4836 to 127.0327),
        "선릉/삼성/논현" to (37.5045 to 127.0490),
        "신사/압구정/청담" to (37.5241 to 127.0366),
        "중량" to (37.6063 to 127.0931),
        "양재/수서/도곡" to (37.4841 to 127.0437),
        "잠실/송파" to (37.5145 to 127.1058),
        "강동/천호" to (37.5301 to 127.1238),
        "건대/성수/왕십리" to (37.5408 to 127.0691),
        "종로" to (37.5730 to 126.9794),
        "홍대/합정/마포" to (37.5567 to 126.9235),
        "중구" to (37.5635 to 126.9975),
        "영등포" to (37.5264 to 126.8962),
        "여의도" to (37.5216 to 126.9242),
        "마곡/강서" to (37.5518 to 126.8495),
        "동작" to (37.5024 to 126.9393),
        "성북/노원" to (37.6068 to 127.0467),
        "구로/관악" to (37.4854 to 126.9015),
    )

    fun getLatLng(regionName: String): Pair<Double, Double>? = regionCoords[regionName]
}
