package org.app.presentation.pubdetail.model

enum class KboTeamType(
    val id: Int,
    val shortName: String,
    val fullName: String,
) {
    ALL(0, "전구단", "전구단 상영"),
    LG(1, "LG", "LG 트윈스"),
    DOOSAN(2, "두산", "두산 베어스"),
    KT(3, "KT", "KT 위즈"),
    SSG(4, "SSG", "SSG 랜더스"),
    NC(5, "NC", "NC 다이노스"),
    KIA(6, "KIA", "KIA 타이거즈"),
    LOTTE(7, "롯데", "롯데 자이언츠"),
    SAMSUNG(8, "삼성", "삼성 라이온즈"),
    HANWHA(9, "한화", "한화 이글스"),
    KIWOOM(10, "키움", "키움 히어로즈"),
    ;

    companion object {
        /** 백엔드 V2 시드 기준 (PR #63) */
        fun fromId(id: Int): KboTeamType = entries.find { it.id == id } ?: ALL

        fun fromShortName(name: String): KboTeamType = entries.find { it.shortName == name } ?: ALL
    }
}
