package org.app.core.analytics

/**
 * 앱 전역 이벤트 로깅 추상화. 구현체는 Firebase Analytics([FirebaseAnalyticsHelper]).
 * ViewModel 등에서 이 인터페이스만 주입받아 사용한다 (Firebase 직접 의존 X → 테스트/교체 용이).
 */
interface AnalyticsHelper {
    fun logEvent(
        name: String,
        params: Map<String, Any> = emptyMap(),
    )
}

/** 커스텀 이벤트 이름 (지표 체계 기준). Firebase 규칙: 영문/숫자/_ , 40자 이내. */
object AnalyticsEvent {
    /** 핵심지표: 펍 상세를 1회+ 조회 */
    const val PUB_DETAIL_VIEW = "pub_detail_view"

    /** Activation: 온보딩(닉네임+응원팀) 완료 */
    const val ONBOARDING_COMPLETE = "onboarding_complete"

    /** Activation: 필터 적용 */
    const val FILTER_APPLY = "filter_apply"

    /** Activation: 검색 실행 */
    const val MAP_SEARCH = "map_search"

    /** Referral/참여: 제보 전송 */
    const val REPORT_SUBMIT = "report_submit"

    /** Referral/참여: 찜(즐겨찾기) 추가 */
    const val FAVORITE_ADD = "favorite_add"

    /** Engagement: 지도 링크(카카오/네이버) 클릭 — 전환에 가장 근접 */
    const val MAP_LINK_CLICK = "map_link_click"
}

/** 자주 쓰는 파라미터 키 */
object AnalyticsParam {
    const val PUB_ID = "pub_id"
    const val MAP_TYPE = "map_type"
    const val CATEGORY = "category"
    const val TEAM_COUNT = "team_count"
    const val REGION_COUNT = "region_count"
    const val RESULT_COUNT = "result_count"
}
