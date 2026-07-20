package org.app.presentation.home.model

import org.app.data.model.PubMapItem

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
 * @param items     클러스터 내 포함된 펍 리스트
 */
data class PubCluster(
    val latitude: Double,
    val longitude: Double,
    val count: Int,
    val items: List<PubMapItem> = emptyList(),
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
    val styleCodes: List<String>? = null,
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
 * 칩 라벨 표시용. 단일 출처는 [SeoulRegion].
 */
val REGION_DISPLAY_LABELS: Map<String, String> = SeoulRegion.displayLabels

object RegionMapper {
    /** 백엔드 region 코드 → 대표 위경도 (지도 카메라 이동용). 단일 출처는 [SeoulRegion]. */
    fun getLatLng(regionCode: String): Pair<Double, Double>? =
        SeoulRegion.fromCode(regionCode)?.let { it.lat to it.lng }
}
