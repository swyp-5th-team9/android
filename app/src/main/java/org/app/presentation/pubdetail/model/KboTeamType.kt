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
    }
}
