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
import org.app.presentation.pubdetail.model.KboTeamType

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
