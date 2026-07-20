package org.app.presentation.schedule.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.data.model.TeamItem

@Composable
fun ScheduleTeamChipBar(
    teams: List<TeamItem>,
    selectedTeamId: Long?,
    onTeamSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    // null = 전체, 나머지는 API teamId
    val items: List<Pair<Long?, String>> = remember(teams) {
        listOf<Pair<Long?, String>>(null to "전체") + teams.map { it.teamId to it.shortName }
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) {
        items(items, key = { it.first?.toString() ?: "all" }) { (teamId, label) ->
            ScheduleTeamChip(
                label = label,
                isSelected = teamId == selectedTeamId,
                onClick = { onTeamSelected(teamId) },
            )
        }
    }
}

@Composable
internal fun ScheduleTeamChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(60.dp)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MoballTheme.colors.accentTertiaryLight else MoballTheme.colors.backgroundBase,
                ).border(
                    width = 1.dp,
                    color = if (isSelected) MoballTheme.colors.accentPrimaryDark else MoballTheme.colors.borderStrong,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MoballTheme.typography.heading6.bold16,
                color = MoballTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
        }

        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_calender_star),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.align(Alignment.TopStart),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ScheduleTeamChipBarPreview() {
    MoballTheme {
        ScheduleTeamChipBar(
            teams = listOf(
                TeamItem(1L, "LG 트윈스", "LG", "KBO", null),
                TeamItem(2L, "두산 베어스", "두산", "KBO", null),
                TeamItem(6L, "KIA 타이거즈", "KIA", "KBO", null),
            ),
            selectedTeamId = null,
            onTeamSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ScheduleTeamChipStatesPreview() {
    MoballTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            ScheduleTeamChip(label = "전체", isSelected = true, onClick = {})
            ScheduleTeamChip(label = "두산", isSelected = false, onClick = {})
            ScheduleTeamChip(label = "LG", isSelected = false, onClick = {})
            ScheduleTeamChip(label = "KIA", isSelected = false, onClick = {})
        }
    }
}
