package org.app.presentation.pubdetail.model

enum class KboTeamType(
    val shortName: String,
    val fullName: String,
) {
    ALL("전구단", "전구단 상영"),
    KIA("KIA", "KIA 타이거즈"),
    KT("KT", "KT 위즈"),
    LG("LG", "LG 트윈스"),
    NC("NC", "NC 다이노스"),
    SSG("SSG", "SSG 랜더스"),
    DOOSAN("두산", "두산 베어스"),
    LOTTE("롯데", "롯데 자이언츠"),
    SAMSUNG("삼성", "삼성 라이온즈"),
    KIWOOM("키움", "키움 히어로즈"),
    HANWHA("한화", "한화 이글스"),
    ;

    companion object {
        /** 백엔드 V2 시드 기준 (PR #63) */
        fun fromId(id: Int): KboTeamType =
            when (id) {
                1 -> LG
                2 -> DOOSAN
                3 -> KT
                4 -> SSG
                5 -> NC
                6 -> KIA
                7 -> LOTTE
                8 -> SAMSUNG
                9 -> HANWHA
                10 -> KIWOOM
                else -> ALL
            }
    }
}
