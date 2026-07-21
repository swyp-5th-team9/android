package org.app.presentation.home.model

/**
 * 서울 지역 필터의 단일 출처.
 *
 * 백엔드 region/sub_region enum 코드(code)를 그대로 필터 파라미터로 사용한다.
 * - SEOUL_ALL → API region = null (전체)
 * - sub-region (JAMSIL / HONGDAE_HAPJEONG / SANGAM_MANGWON) 은 서버에서 교집합 필터 적용
 *
 * 라벨 표시, 지도 카메라 이동(위경도), 필터 옵션 목록이 모두 이 enum에서 파생된다.
 * (기존에 흩어져 있던 REGION_DISPLAY_LABELS / RegionMapper / SEOUL_SUB_REGIONS / REGIONS 를 대체)
 *
 * @param code  백엔드 region 코드
 * @param label 한글 표시 라벨
 * @param lat   대표 위도 (지도 카메라 이동용)
 * @param lng   대표 경도
 */
enum class SeoulRegion(
    val code: String,
    val label: String,
    val lat: Double,
    val lng: Double,
) {
    SEOUL_ALL("SEOUL_ALL", "서울 전체", 37.5665, 126.9780),
    GANGNAM("GANGNAM", "강남/서초", 37.4981, 127.0276),
    JAMSIL("JAMSIL", "잠실/잠실새내", 37.5145, 127.1058), // sub-region (송파)
    SONGPA("SONGPA", "송파", 37.5049, 127.1154),
    GANGDONG("GANGDONG", "강동/천호", 37.5301, 127.1238),
    SEONGDONG("SEONGDONG", "성수/왕십리", 37.5408, 127.0691),
    JUNGNANG("JUNGNANG", "중랑", 37.6063, 127.0931),
    JONGNO("JONGNO", "종로", 37.5730, 126.9794),
    JUNGGU("JUNGGU", "중구", 37.5635, 126.9975),
    HONGDAE_HAPJEONG("HONGDAE_HAPJEONG", "홍대/합정", 37.5567, 126.9235), // sub-region (마포)
    SANGAM_MANGWON("SANGAM_MANGWON", "상암/망원", 37.5605, 126.9097), // sub-region (마포)
    MAPO("MAPO", "마포", 37.5538, 126.9510),
    EUNPYEONG("EUNPYEONG", "은평/서대문", 37.6027, 126.9289), // 은평+서대문 커버
    YEONGDEUNGPO("YEONGDEUNGPO", "영등포/여의도", 37.5264, 126.8962),
    GANGSEO("GANGSEO", "마곡/강서", 37.5518, 126.8495), // 강서+양천 커버
    DONGJAK("DONGJAK", "동작", 37.5024, 126.9393),
    NOWON("NOWON", "노원/강북", 37.6539, 127.0569), // 노원+강북 커버
    DOBONG("DOBONG", "도봉/성북", 37.6543, 127.0472), // 도봉+성북 커버
    GURO("GURO", "구로", 37.4954, 126.8874),
    GWANAK("GWANAK", "관악", 37.4784, 126.9516),
    ;

    companion object {
        private val byCode: Map<String, SeoulRegion> = entries.associateBy { it.code }

        fun fromCode(code: String): SeoulRegion? = byCode[code]

        /** 백엔드 region 코드 → 한글 표시 라벨 */
        val displayLabels: Map<String, String> = entries.associate { it.code to it.label }
    }
}
