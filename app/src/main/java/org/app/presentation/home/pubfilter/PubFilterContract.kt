package org.app.presentation.home.pubfilter

import org.app.presentation.home.model.PubFilterOption
import org.app.presentation.home.model.PubFilterSection

interface PubFilterContract {
    data class State(
        val sections: List<PubFilterSection> = defaultSections(),
        /** sectionId → Set<optionId> */
        val selectedOptions: Map<String, Set<String>> = emptyMap(),
        val isLoading: Boolean = false,
    ) {
        val hasSelection: Boolean
            get() = selectedOptions.values.any { it.isNotEmpty() }
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
            val teamNames: List<String>,
            val region: String?,
        ) : SideEffect
    }
}

internal val SEOUL_SUB_REGIONS: List<PubFilterOption> = listOf(
    PubFilterOption("seoul_all", "서울 전체"),
    PubFilterOption("seoul_gangnam", "강남/역삼"),
    PubFilterOption("seoul_seocho", "서초/방배/반포"),
    PubFilterOption("seoul_seolleung", "선릉/삼성/논현"),
    PubFilterOption("seoul_sinsa", "신사/압구정/청담"),
    PubFilterOption("seoul_jungnang", "중량"),
    PubFilterOption("seoul_yangjae", "양재/수서/도곡"),
    PubFilterOption("seoul_jamsil", "잠실/송파"),
    PubFilterOption("seoul_gangdong", "강동/천호"),
    PubFilterOption("seoul_kondae", "건대/성수/왕십리"),
    PubFilterOption("seoul_jongno", "종로"),
    PubFilterOption("seoul_hongdae", "홍대/합정/마포"),
    PubFilterOption("seoul_junggu", "중구"),
    PubFilterOption("seoul_yeongdeungpo", "영등포"),
    PubFilterOption("seoul_yeouido", "여의도"),
    PubFilterOption("seoul_magok", "마곡/강서"),
    PubFilterOption("seoul_dongjak", "동작"),
    PubFilterOption("seoul_seongbuk", "성북/노원"),
    PubFilterOption("seoul_guro", "구로/관악"),
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
        PubFilterSection(
            sectionId = "style_broadcast",
            title = "경기 상영 스타일",
            options = listOf(
                PubFilterOption("large_screen", "대형 스크린"),
                PubFilterOption("single_tv", "단일 TV"),
                PubFilterOption("multi_tv", "멀티 TV"),
                PubFilterOption("broadcast_sound", "중계 사운드"),
            ),
        ),
        PubFilterSection(
            sectionId = "style_facility",
            title = "시설 / 서비스",
            options = listOf(
                PubFilterOption("group_seat", "단체석"),
                PubFilterOption("wide_space", "넓은 공간"),
                PubFilterOption("outdoor_seat", "야외좌석"),
                PubFilterOption("parking", "주차"),
                PubFilterOption("reservation", "예약가능"),
                PubFilterOption("rental", "대관가능"),
            ),
        ),
        PubFilterSection(
            sectionId = "style_theme",
            title = "테마",
            options = listOf(
                PubFilterOption("exotic", "이국적인"),
                PubFilterOption("wide_space_theme", "넓은 공간"),
                PubFilterOption("special_menu", "특별한 메뉴"),
                PubFilterOption("fresh", "신선한"),
                PubFilterOption("comfortable_seat", "편한 좌석"),
            ),
        ),
        PubFilterSection(
            sectionId = "food",
            title = "음식 / 주류",
            options = listOf(
                PubFilterOption("chicken", "치킨"),
                PubFilterOption("pizza", "피자"),
                PubFilterOption("taco", "타코"),
                PubFilterOption("fries", "감자튀김"),
                PubFilterOption("snack", "분식"),
                PubFilterOption("grill", "구이 요리"),
                PubFilterOption("dried_snack", "마른안주"),
                PubFilterOption("beer", "맥주"),
                PubFilterOption("cocktail", "칵테일"),
                PubFilterOption("highball", "하이볼"),
                PubFilterOption("soju", "소주"),
            ),
        ),
    )
