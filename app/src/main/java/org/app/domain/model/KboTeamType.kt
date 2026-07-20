package org.app.presentation.pubdetail.model

import androidx.annotation.DrawableRes
import com.moball.app.R

enum class KboTeamType(
    val id: Int,
    val shortName: String,
    val fullName: String,
    /** 연고지. ALL(전구단)은 해당 없음("") */
    val city: String,
    /** 구단 로고. ALL(전구단)은 로고를 쓰지 않으므로 null */
    @DrawableRes val logoRes: Int?,
) {
    ALL(0, "전구단", "전구단 상영", "", null),
    LG(1, "LG", "LG 트윈스", "서울", R.drawable.img_lg),
    DOOSAN(2, "두산", "두산 베어스", "서울", R.drawable.img_doosan),
    KT(3, "KT", "KT 위즈", "수원", R.drawable.img_kt),
    SSG(4, "SSG", "SSG 랜더스", "인천", R.drawable.img_ssg),
    NC(5, "NC", "NC 다이노스", "창원", R.drawable.img_nc),
    KIA(6, "KIA", "KIA 타이거즈", "광주", R.drawable.img_kia),
    LOTTE(7, "롯데", "롯데 자이언츠", "부산", R.drawable.img_lotte),
    SAMSUNG(8, "삼성", "삼성 라이온즈", "대구", R.drawable.img_samsung),
    HANWHA(9, "한화", "한화 이글스", "대전", R.drawable.img_hanwha),
    KIWOOM(10, "키움", "키움 히어로즈", "서울", R.drawable.img_kiwoom),
    ;

    companion object {
        /** 전구단(ALL)을 제외한 실제 10개 구단. 팀 선택/표시 UI의 단일 출처 */
        val teams: List<KboTeamType> = entries.filter { it != ALL }

        /** 백엔드 V2 시드 기준 (PR #63) */
        fun fromId(id: Int): KboTeamType = entries.find { it.id == id } ?: ALL

        fun fromShortName(name: String): KboTeamType = entries.find { it.shortName == name } ?: ALL

        /**
         * id → shortName → 팀명 부분 일치 순으로 매칭. 전부 실패하면 null.
         * (서버 응답의 ID 체계·필드명이 달라져도 팀명 문자열로 복구하기 위한 안전망)
         */
        fun matchOrNull(
            id: Int? = null,
            shortName: String? = null,
            fullName: String? = null,
        ): KboTeamType? {
            // 1. ID 매칭 (최우선)
            val byId = if (id != null && id > 0) entries.find { it.id == id && it != ALL } else null
            if (byId != null) return byId

            // 2. ShortName 매칭 (대소문자 무시, 공백 제거)
            val trimmedShort = shortName?.trim()?.uppercase().orEmpty()
            if (trimmedShort.isNotEmpty()) {
                val byShortName = entries.find {
                    it != ALL &&
                        (it.shortName.uppercase() == trimmedShort || trimmedShort.contains(it.shortName.uppercase()))
                }
                if (byShortName != null) return byShortName
            }

            // 3. FullName 매칭 (부분 일치 포함)
            val trimmedFull = fullName?.trim()?.uppercase().orEmpty()
            if (trimmedFull.isNotEmpty()) {
                val byFullName = entries.find {
                    it != ALL &&
                        (
                            trimmedFull.contains(it.shortName.uppercase()) ||
                                it.fullName.uppercase() == trimmedFull ||
                                trimmedFull.contains(it.fullName.uppercase())
                        )
                }
                if (byFullName != null) return byFullName
            }

            // 4. 특정 키워드 수동 매칭 (예외 케이스 및 별명)
            val combined = (trimmedShort + trimmedFull)
            return when {
                combined.contains("두산") || combined.contains("DOOSAN") -> DOOSAN
                combined.contains("LG") || combined.contains("엘지") -> LG
                combined.contains("KIA") || combined.contains("기아") -> KIA
                combined.contains("SSG") || combined.contains("쓱") || combined.contains("SSING") -> SSG
                combined.contains("NC") || combined.contains("엔씨") -> NC
                combined.contains("KT") || combined.contains("케이티") -> KT
                combined.contains("삼성") || combined.contains("SAMSUNG") -> SAMSUNG
                combined.contains("롯데") || combined.contains("LOTTE") -> LOTTE
                combined.contains("한화") || combined.contains("HANWHA") -> HANWHA
                combined.contains("키움") || combined.contains("KIWOOM") -> KIWOOM
                else -> null
            }
        }
    }
}
