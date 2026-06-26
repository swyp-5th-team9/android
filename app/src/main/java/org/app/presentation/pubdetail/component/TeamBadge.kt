package org.app.presentation.pubdetail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme

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

val KboTeamType.teamColor: Color
    @Composable
    get() = when (this) {
        KboTeamType.ALL -> MoballTheme.colors.teamColorAll
        KboTeamType.KIA -> MoballTheme.colors.teamColorKia
        KboTeamType.KT -> MoballTheme.colors.teamColorKt
        KboTeamType.LG -> MoballTheme.colors.teamColorLg
        KboTeamType.NC -> MoballTheme.colors.teamColorNc
        KboTeamType.SSG -> MoballTheme.colors.teamColorSsg
        KboTeamType.DOOSAN -> MoballTheme.colors.teamColorDoosan
        KboTeamType.LOTTE -> MoballTheme.colors.teamColorLotte
        KboTeamType.SAMSUNG -> MoballTheme.colors.teamColorSamsung
        KboTeamType.KIWOOM -> MoballTheme.colors.teamColorKiwoom
        KboTeamType.HANWHA -> MoballTheme.colors.teamColorHanwha
    }

/**
 * 펍 상세 상단 팀 배지 — 테두리+텍스트만, 채움 없음 (pill shape)
 */
@Composable
fun TeamBadge(
    teamType: KboTeamType,
    modifier: Modifier = Modifier,
) {
    val color = teamType.teamColor
    Surface(
        modifier = modifier.height(28.dp),
        shape = RoundedCornerShape(999.dp),
        color = Color.Transparent,
        border = BorderStroke(width = 1.dp, color = color),
    ) {
        Text(
            text = teamType.fullName,
            style = MoballTheme.typography.caption.medium12,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
    }
}

/**
 * 찜목록 등 리스트에서 사용하는 팀 배지 — 연두 배경 + 진녹색 텍스트, 4px radius
 */
@Composable
fun TeamListBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(24.dp),
        shape = RoundedCornerShape(4.dp),
        color = MoballTheme.colors.teamListBadgeBg,
    ) {
        Text(
            text = text,
            style = MoballTheme.typography.caption.medium12,
            color = MoballTheme.colors.teamListBadgeText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TeamBadgePreview() {
    MoballTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KboTeamType.entries.forEach { team ->
                    TeamBadge(teamType = team)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamListBadge(text = "LG 트윈스")
                TeamListBadge(text = "두산 베어스")
            }
        }
    }
}
