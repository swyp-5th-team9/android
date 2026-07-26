package org.app.presentation.home.pubfilter

import org.app.presentation.home.model.PubFilterOption

/**
 * 필터 섹션 id (State.selectedOptions 의 키).
 * 문자열 오타로 필터가 조용히 깨지는 것을 막기 위한 단일 출처.
 */
object FilterSectionId {
    const val TEAM = "team"
    const val REGION = "region"
    const val BUSINESS = "business"
    const val BUSINESS_DAY = "business_day"
    const val STYLE_BROADCAST = "style_broadcast"
    const val STYLE_FACILITY = "style_facility"
    const val STYLE_THEME = "style_theme"
    const val FOOD = "food"
}

/** 필터 옵션 코드 공통 계약 — optionId(=서버 화이트리스트 코드)와 표시 라벨 */
interface FilterCode {
    /** 서버로 그대로 전달되는 코드. PubFilterOption.id 로 사용 */
    val code: String
    val label: String
}

/** 필터 코드 목록을 그대로 PubFilterOption 목록으로 변환 */
fun List<FilterCode>.toFilterOptions(): List<PubFilterOption> = map { PubFilterOption(it.code, it.label) }

/** 경기 상영 스타일 (styleCodes 파라미터) */
enum class BroadcastStyleCode(
    override val code: String,
    override val label: String,
) : FilterCode {
    BIG_SCREEN("BIG_SCREEN", "대형 스크린"),
    SINGLE_TV("SINGLE_TV", "단일 TV"),
    MULTI_TV("MULTI_TV", "멀티 TV"),
    LOUD_SPEAKER("LOUD_SPEAKER", "중계 사운드"),
}

/** 시설 / 서비스 (facilityCodes 파라미터) */
enum class FacilityCode(
    override val code: String,
    override val label: String,
) : FilterCode {
    GROUP_SEAT("GROUP_SEAT", "단체석"),
    SPACIOUS_AREA("SPACIOUS_AREA", "넓은 공간"),
    OUTDOOR_SEAT("OUTDOOR_SEAT", "야외좌석"),
    PARKING("PARKING", "주차"),
    RESERVATION("RESERVATION", "예약가능"),
    PRIVATE_BOOKING("PRIVATE_BOOKING", "대관가능"),
}

/** 테마 (themeCodes 파라미터) */
enum class ThemeCode(
    override val code: String,
    override val label: String,
) : FilterCode {
    EXOTIC("EXOTIC", "이국적인"),
    SPACIOUS_VIEW("SPACIOUS_VIEW", "넓은 공간"),
    SPECIAL_MENU("SPECIAL_MENU", "특별한 메뉴"),
    FRESH("FRESH", "신선한"),
    COMFY_SEAT("COMFY_SEAT", "편한 좌석"),
}

/** 음식 / 주류 (foodCodes 파라미터). 라벨·순서는 시안(=서버 스펙) 기준 */
enum class FoodCode(
    override val code: String,
    override val label: String,
) : FilterCode {
    CHICKEN("CHICKEN", "치킨"),
    PIZZA("PIZZA", "피자"),
    TACO("TACO", "타코"),
    FRY("FRY", "튀김"),
    STEW("STEW", "찌개/탕"),
    GRILLED("GRILLED", "구이/볶음"),
    BUNSIK("BUNSIK", "분식"),
    DRY_SNACK("DRY_SNACK", "마른안주"),
    SOJU("SOJU", "소주"),
    BEER("BEER", "맥주"),
    COCKTAIL("COCKTAIL", "칵테일"),
    HIGHBALL("HIGHBALL", "하이볼"),
    ;

    companion object {
        /** 주류 코드 집합 — "다양한 술" 퀵 필터의 토글/선택 판정 단일 출처 */
        val DRINK_CODES: Set<String> = setOf(SOJU.code, BEER.code, COCKTAIL.code, HIGHBALL.code)
    }
}

/**
 * 영업 요일 필터. 클라이언트 optionId ↔ 서버 코드 매핑을 한곳에 둔다.
 * ALL_DAYS(전체)는 서버 코드가 없다(필터 없음).
 */
enum class BusinessDayFilter(
    val optionId: String,
    val serverCode: String?,
    val label: String,
) {
    ALL_DAYS("all_days", null, "전체"),
    WEEKDAYS("weekdays", "WEEKDAY", "평일 모두"),
    WEEKENDS("weekends", "WEEKEND", "주말 모두"),
    ALWAYS_OPEN("always_open", "EVERYDAY", "연중무휴"),
    MON("mon", "MON", "월"),
    TUE("tue", "TUE", "화"),
    WED("wed", "WED", "수"),
    THU("thu", "THU", "목"),
    FRI("fri", "FRI", "금"),
    SAT("sat", "SAT", "토"),
    SUN("sun", "SUN", "일"),
    ;

    companion object {
        /** optionId → 서버 코드. 매칭 실패/전체(all_days) 시 null */
        fun serverCodeOf(optionId: String?): String? = entries.firstOrNull { it.optionId == optionId }?.serverCode

        /** 서버 코드 → optionId. 적용된 필터를 필터 화면에 복원(시드)할 때 사용 */
        fun optionIdOf(serverCode: String?): String? =
            if (serverCode == null) null else entries.firstOrNull { it.serverCode == serverCode }?.optionId

        val options: List<PubFilterOption> = entries.map { PubFilterOption(it.optionId, it.label) }
    }
}
