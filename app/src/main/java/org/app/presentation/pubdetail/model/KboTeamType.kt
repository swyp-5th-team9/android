package org.app.presentation.pubdetail.model

enum class KboTeamType(
    val fullName: String,
) {
    ALL("전구단 상영"),
    KIA("KIA 타이거즈"),
    KT("KT 위즈"),
    LG("LG 트윈스"),
    NC("NC 다이노스"),
    SSG("SSG 랜더스"),
    DOOSAN("두산 베어스"),
    LOTTE("롯데 자이언츠"),
    SAMSUNG("삼성 라이온즈"),
    KIWOOM("키움 히어로즈"),
    HANWHA("한화 이글스"),
    ;

    companion object {
        fun fromId(id: Int): KboTeamType =
            when (id) {
                1 -> KIA
                2 -> KT
                3 -> LG
                4 -> NC
                5 -> SSG
                6 -> DOOSAN
                7 -> LOTTE
                8 -> SAMSUNG
                9 -> KIWOOM
                10 -> HANWHA
                else -> ALL
            }
    }
}
