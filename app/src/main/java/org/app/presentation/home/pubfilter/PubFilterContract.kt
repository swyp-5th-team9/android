package org.app.presentation.home.pubfilter

import org.app.data.model.TeamItem
import org.app.presentation.home.model.PubFilterOption
import org.app.presentation.home.model.PubFilterSection

interface PubFilterContract {
    data class State(
        /** GET /api/v1/teams 응답 — 로드 후 team 섹션을 동적으로 구성 */
        val teams: List<TeamItem> = emptyList(),
        val sections: List<PubFilterSection> = defaultSections(),
        /** sectionId → Set<optionId> */
        val selectedOptions: Map<String, Set<String>> = emptyMap(),
        val isLoading: Boolean = false,
    ) {
        val hasSelection: Boolean
            get() = selectedOptions.values.any { it.isNotEmpty() }

        /** teams 로드 후 team 섹션 옵션을 동적으로 교체한 섹션 목록 */
        val resolvedSections: List<PubFilterSection>
            get() {
                if (teams.isEmpty()) return sections
                val teamSection = PubFilterSection(
                    sectionId = "team",
                    title = "응원팀",
                    options = listOf(PubFilterOption("all", "KBO 전체")) +
                        teams.map { PubFilterOption(it.teamId.toString(), it.shortName) },
                )
                return sections.map { if (it.sectionId == "team") teamSection else it }
            }
    }

    sealed interface Event {
        data object OnBack : Event

        data object OnReset : Event

        data object OnApply : Event

        data class OnOptionToggle(
            val sectionId: String,
            val optionId: String,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        data class ApplyFilter(
            val selected: Map<String, Set<String>>,
            val teamIds: List<Long>,
            val teamNames: List<String>,
            val regions: List<String>,
            val openNow: Boolean?,
            val businessDay: String?,
            val facilityCodes: List<String>,
            val styleCodes: List<String>,
            val themeCodes: List<String>,
            val foodCodes: List<String>,
        ) : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}

/**
 * 백엔드 region/sub_region enum 코드를 optionId로 사용.
 * SEOUL_ALL → API region = null (전체)
 * 그 외 → API region = optionId 그대로 전달
 * sub-region (JAMSIL / HONGDAE_HAPJEONG / SANGAM_MANGWON) 은 서버에서 교집합 필터 적용
 */
internal val SEOUL_SUB_REGIONS: List<PubFilterOption> = listOf(
    PubFilterOption("SEOUL_ALL", "서울 전체"),
    PubFilterOption("GANGNAM", "강남/서초"),
    PubFilterOption("JAMSIL", "잠실/잠실새내"), // sub-region (송파)
    PubFilterOption("SONGPA", "송파"),
    PubFilterOption("GANGDONG", "강동/천호"),
    PubFilterOption("SEONGDONG", "성수/왕십리"),
    PubFilterOption("JUNGNANG", "중랑"),
    PubFilterOption("JONGNO", "종로"),
    PubFilterOption("JUNGGU", "중구"),
    PubFilterOption("HONGDAE_HAPJEONG", "홍대/합정"), // sub-region (마포)
    PubFilterOption("SANGAM_MANGWON", "상암/망원"), // sub-region (마포) — 신규
    PubFilterOption("MAPO", "마포"),
    PubFilterOption("EUNPYEONG", "은평/서대문"), // 은평+서대문 커버
    PubFilterOption("YEONGDEUNGPO", "영등포/여의도"),
    PubFilterOption("GANGSEO", "마곡/강서"), // 강서+양천 커버
    PubFilterOption("DONGJAK", "동작"),
    PubFilterOption("NOWON", "노원/강북"), // 노원+강북 커버
    PubFilterOption("DOBONG", "도봉/성북"), // 도봉+성북 커버
    PubFilterOption("GURO", "구로"),
    PubFilterOption("GWANAK", "관악"),
)

internal data class CityOption(
    val id: String,
    val label: String,
    val isEnabled: Boolean,
)

internal val CITIES: List<CityOption> = listOf(
    CityOption("seoul", "서울", isEnabled = true),
    CityOption("gyeonggi", "경기", isEnabled = false),
    CityOption("incheon", "인천", isEnabled = false),
    CityOption("busan", "부산", isEnabled = false),
)

private fun defaultSections(): List<PubFilterSection> =
    listOf(
        PubFilterSection(
            sectionId = "region",
            title = "지역",
            options = SEOUL_SUB_REGIONS,
        ),
        PubFilterSection(
            sectionId = "business",
            title = "영업상태",
            options = listOf(
                PubFilterOption("open", "영업 중"),
                PubFilterOption("all", "전체"),
            ),
        ),
        PubFilterSection(
            sectionId = "business_day",
            title = "영업 요일",
            options = listOf(
                PubFilterOption("all_days", "전체"),
                PubFilterOption("weekdays", "평일 모두"),
                PubFilterOption("weekends", "주말 모두"),
                PubFilterOption("always_open", "연중무휴"),
                PubFilterOption("mon", "월"),
                PubFilterOption("tue", "화"),
                PubFilterOption("wed", "수"),
                PubFilterOption("thu", "목"),
                PubFilterOption("fri", "금"),
                PubFilterOption("sat", "토"),
                PubFilterOption("sun", "일"),
            ),
        ),
        PubFilterSection(
            sectionId = "team",
            title = "응원팀",
            options = listOf(
                PubFilterOption("all", "KBO 전체"),
                PubFilterOption("kia", "KIA"),
                PubFilterOption("samsung", "삼성"),
                PubFilterOption("lg", "LG"),
                PubFilterOption("doosan", "두산"),
                PubFilterOption("kt", "KT"),
                PubFilterOption("ssg", "SSG"),
                PubFilterOption("lotte", "롯데"),
                PubFilterOption("hanwha", "한화"),
                PubFilterOption("nc", "NC"),
                PubFilterOption("kiwoom", "키움"),
            ),
        ),
        // optionId = 서버 화이트리스트 코드(그대로 styleCodes 파라미터로 전달)
        PubFilterSection(
            sectionId = "style_broadcast",
            title = "경기 상영 스타일",
            options = listOf(
                PubFilterOption("BIG_SCREEN", "대형 스크린"),
                PubFilterOption("SINGLE_TV", "단일 TV"),
                PubFilterOption("MULTI_TV", "멀티 TV"),
                PubFilterOption("LOUD_SPEAKER", "중계 사운드"),
            ),
        ),
        // optionId = 서버 화이트리스트 코드(그대로 facilityCodes 파라미터로 전달)
        PubFilterSection(
            sectionId = "style_facility",
            title = "시설 / 서비스",
            options = listOf(
                PubFilterOption("GROUP_SEAT", "단체석"),
                PubFilterOption("SPACIOUS_AREA", "넓은 공간"),
                PubFilterOption("OUTDOOR_SEAT", "야외좌석"),
                PubFilterOption("PARKING", "주차"),
                PubFilterOption("RESERVATION", "예약가능"),
                PubFilterOption("PRIVATE_BOOKING", "대관가능"),
            ),
        ),
        // optionId = 서버 화이트리스트 코드(그대로 themeCodes 파라미터로 전달)
        PubFilterSection(
            sectionId = "style_theme",
            title = "테마",
            options = listOf(
                PubFilterOption("EXOTIC", "이국적인"),
                PubFilterOption("SPACIOUS_VIEW", "넓은 공간"),
                PubFilterOption("SPECIAL_MENU", "특별한 메뉴"),
                PubFilterOption("FRESH", "신선한"),
                PubFilterOption("COMFY_SEAT", "편한 좌석"),
            ),
        ),
        // optionId = 서버 화이트리스트 코드(그대로 foodCodes 파라미터로 전달)
        // 라벨·순서는 시안(=서버 스펙) 기준
        PubFilterSection(
            sectionId = "food",
            title = "음식 / 주류",
            options = listOf(
                PubFilterOption("CHICKEN", "치킨"),
                PubFilterOption("PIZZA", "피자"),
                PubFilterOption("TACO", "타코"),
                PubFilterOption("FRY", "튀김"),
                PubFilterOption("STEW", "찌개/탕"),
                PubFilterOption("GRILLED", "구이/볶음"),
                PubFilterOption("BUNSIK", "분식"),
                PubFilterOption("DRY_SNACK", "마른안주"),
                PubFilterOption("SOJU", "소주"),
                PubFilterOption("BEER", "맥주"),
                PubFilterOption("COCKTAIL", "칵테일"),
                PubFilterOption("HIGHBALL", "하이볼"),
            ),
        ),
    )
