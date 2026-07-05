package org.app.presentation.pubdetail.model

import androidx.annotation.DrawableRes
import com.moball.app.R

enum class KboTeamType(
    val id: Int,
    val shortName: String,
    val fullName: String,
    @DrawableRes val logoRes: Int,
) {
    ALL(0, "전구단", "전구단 상영", R.drawable.img_doosan),
    LG(1, "LG", "LG 트윈스", R.drawable.img_lg),
    DOOSAN(2, "두산", "두산 베어스", R.drawable.img_doosan),
    KT(3, "KT", "KT 위즈", R.drawable.img_kt),
    SSG(4, "SSG", "SSG 랜더스", R.drawable.img_ssg),
    NC(5, "NC", "NC 다이노스", R.drawable.img_nc),
    KIA(6, "KIA", "KIA 타이거즈", R.drawable.img_kia),
    LOTTE(7, "롯데", "롯데 자이언츠", R.drawable.img_lotte),
    SAMSUNG(8, "삼성", "삼성 라이온즈", R.drawable.img_samsung),
    HANWHA(9, "한화", "한화 이글스", R.drawable.img_hanwha),
    KIWOOM(10, "키움", "키움 히어로즈", R.drawable.img_kiwoom),
    ;

    companion object {
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
            val byId = if (id != null) entries.find { it.id == id && it != ALL } else null
            if (byId != null) return byId

            val trimmedShort = shortName?.trim().orEmpty()
            if (trimmedShort.isNotEmpty()) {
                val byShortName = entries.find { it != ALL && it.shortName.equals(trimmedShort, ignoreCase = true) }
                if (byShortName != null) return byShortName
            }

            val trimmedFull = fullName?.trim().orEmpty()
            if (trimmedFull.isNotEmpty()) {
                val byFullName = entries.find {
                    it != ALL && (trimmedFull.contains(it.shortName, ignoreCase = true) || it.fullName == trimmedFull)
                }
                if (byFullName != null) return byFullName
            }
            return null
        }
    }
}
