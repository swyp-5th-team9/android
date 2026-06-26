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

private val KBO_TEAMS: List<Pair<Int?, String>> = listOf(
    null to "전체",
    1 to "KIA",
    2 to "KT",
    3 to "LG",
    4 to "NC",
    5 to "SSG",
    6 to "두산",
    7 to "롯데",
    8 to "삼성",
    9 to "키움",
    10 to "한화",
)

@Composable
fun ScheduleTeamChipBar(
    selectedTeamId: Int?,
    onTeamSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) {
        items(KBO_TEAMS, key = { it.first?.toString() ?: "all" }) { (teamId, label) ->
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
