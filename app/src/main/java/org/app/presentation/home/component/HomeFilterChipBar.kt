package org.app.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R.drawable
import org.app.core.designsystem.component.MoballFilterChip
import org.app.core.designsystem.theme.MoballTheme

/**
 * 홈 지도 상단 필터 칩 바
 *
 * @param teamChipLabel     구단 칩 라벨 (미선택="구단", 선택=구단명)
 * @param regionChipLabel   지역 칩 라벨 (미선택="지역", 선택=지역명)
 * @param isTeamSelected    구단 필터 선택 여부
 * @param isRegionSelected  지역 필터 선택 여부
 * @param onMenuClick       햄버거 칩 클릭 → 필터 전체화면
 * @param onTeamClick       구단 칩 클릭 → 바텀시트
 * @param onRegionClick     지역 칩 클릭 → 바텀시트
 */
@Composable
fun HomeFilterChipBar(
    teamChipLabel: String,
    regionChipLabel: String,
    isTeamSelected: Boolean,
    isRegionSelected: Boolean,
    onMenuClick: () -> Unit,
    onTeamClick: () -> Unit,
    onRegionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MoballFilterChip(
            label = "",
            onClick = onMenuClick,
            leadingIcon = ImageVector.vectorResource(drawable.ic_menu),
            showTrailingIcon = false,
        )

        MoballFilterChip(
            label = teamChipLabel,
            onClick = onTeamClick,
            isSelected = isTeamSelected,
            leadingIcon = ImageVector.vectorResource(drawable.ic_baseball_chip),
        )
        MoballFilterChip(
            label = regionChipLabel,
            onClick = onRegionClick,
            isSelected = isRegionSelected,
            leadingIcon = ImageVector.vectorResource(drawable.ic_location),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFBFC6CF)
@Composable
private fun HomeFilterChipBarDefaultPreview() {
    MoballTheme {
        HomeFilterChipBar(
            teamChipLabel = "구단",
            regionChipLabel = "지역",
            isTeamSelected = false,
            isRegionSelected = false,
            onMenuClick = {},
            onTeamClick = {},
            onRegionClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFBFC6CF)
@Composable
private fun HomeFilterChipBarSelectedPreview() {
    MoballTheme {
        HomeFilterChipBar(
            teamChipLabel = "한화 외 1개",
            regionChipLabel = "강남",
            isTeamSelected = true,
            isRegionSelected = true,
            onMenuClick = {},
            onTeamClick = {},
            onRegionClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
