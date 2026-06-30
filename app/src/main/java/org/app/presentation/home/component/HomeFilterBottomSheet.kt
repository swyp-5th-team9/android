package org.app.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.home.FilterBottomSheetTab
import org.app.presentation.home.pubfilter.component.PubFilterOptionChip
import org.app.presentation.home.pubfilter.component.PubFilterSubRegionChip

private val REGIONS = listOf(
    "서울 전체",
    "강남/역삼",
    "서초/방배/반포",
    "선릉/삼성/논현",
    "신사/압구정/청담",
    "중량",
    "양재/수서/도곡",
    "잠실/송파",
    "강동/천호",
    "건대/성수/왕십리",
    "종로",
    "홍대/합정/마포",
    "중구",
    "영등포",
    "여의도",
    "마곡/강서",
    "동작",
    "성북/노원",
    "구로/관악",
)

/** 백엔드 V2 시드 기준 (teamId: Long) */
private val KBO_TEAMS = listOf(
    0L to "KBO 전체",
    1L to "LG",
    2L to "두산",
    3L to "KT",
    4L to "SSG",
    5L to "NC",
    6L to "KIA",
    7L to "롯데",
    8L to "삼성",
    9L to "한화",
    10L to "키움",
)

/**
 * 홈 화면 전용 필터 바텀시트
 * 응원팀 / 지역 두 탭 — 선택 후 "적용하기" 버튼
 *
 * @param initialTab          처음 표시할 탭
 * @param userFavoriteTeamIds 온보딩에서 선택한 응원 구단 ID (ic_team_favorite 표시)
 * @param initialTeamIds      현재 필터에 적용된 구단 ID
 * @param initialRegion       현재 필터에 적용된 지역
 * @param onApply             (teamIds, teamNames, region?) 콜백
 * @param onDismiss           닫기 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFilterBottomSheet(
    initialTab: FilterBottomSheetTab,
    userFavoriteTeamIds: List<Long>,
    initialTeamIds: List<Long>,
    initialRegion: String?,
    onApply: (teamIds: List<Long>, teamNames: List<String>, region: String?) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val selectedTeamIds = remember { mutableStateListOf<Long>().also { it.addAll(initialTeamIds) } }
    var selectedRegion by remember { mutableStateOf(initialRegion) }
    var currentTab by remember { mutableStateOf(initialTab) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MoballTheme.colors.backgroundBase,
        contentWindowInsets = { WindowInsets(0) },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MoballTheme.colors.borderNormal),
            )
        },
    ) {
        FilterBottomSheetContent(
            currentTab = currentTab,
            userFavoriteTeamIds = userFavoriteTeamIds,
            selectedTeamIds = selectedTeamIds,
            selectedRegion = selectedRegion,
            onTabChange = { currentTab = it },
            onToggleTeam = { teamId ->
                when {
                    teamId == 0L -> {
                        selectedTeamIds.clear()
                        selectedTeamIds.add(0L)
                    }
                    teamId in selectedTeamIds -> {
                        selectedTeamIds.remove(teamId)
                    }
                    else -> {
                        selectedTeamIds.remove(0L)
                        selectedTeamIds.add(teamId)
                    }
                }
            },
            onSelectRegion = { region ->
                selectedRegion = if (selectedRegion == region) null else region
            },
            onApply = {
                val names = KBO_TEAMS
                    .filter { (id, _) -> id in selectedTeamIds }
                    .map { (_, name) -> name }
                onApply(selectedTeamIds.toList(), names, selectedRegion)
            },
            modifier = Modifier.navigationBarsPadding(),
        )
    }
}

@Composable
private fun FilterBottomSheetContent(
    currentTab: FilterBottomSheetTab,
    userFavoriteTeamIds: List<Long>,
    selectedTeamIds: List<Long>,
    selectedRegion: String?,
    onTabChange: (FilterBottomSheetTab) -> Unit,
    onToggleTeam: (Long) -> Unit,
    onSelectRegion: (String) -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            FilterBottomSheetTab.entries.forEach { tab ->
                val label = if (tab == FilterBottomSheetTab.TEAM) "응원팀" else "지역"
                val isActive = tab == currentTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable { onTabChange(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = label,
                        style = MoballTheme.typography.heading6.bold16,
                        color = if (isActive) {
                            MoballTheme.colors.textPrimary
                        } else {
                            MoballTheme.colors.textTertiary
                        },
                        modifier = Modifier.padding(vertical = 14.dp),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                if (isActive) MoballTheme.colors.accentPrimary else MoballTheme.colors.borderNormal,
                            ),
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        when (currentTab) {
            FilterBottomSheetTab.TEAM ->
                TeamFilterContent(
                    favoriteTeamIds = userFavoriteTeamIds,
                    selectedTeamIds = selectedTeamIds,
                    onToggleTeam = onToggleTeam,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

            FilterBottomSheetTab.REGION ->
                RegionFilterContent(
                    selectedRegion = selectedRegion,
                    onSelectRegion = onSelectRegion,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MoballTheme.colors.borderNormal,
                ).padding(horizontal = 16.dp, vertical = 25.dp),
        ) {
            MoballButton(
                text = "적용하기",
                onClick = onApply,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TeamFilterContent(
    favoriteTeamIds: List<Long>,
    selectedTeamIds: List<Long>,
    onToggleTeam: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        KBO_TEAMS.forEach { (teamId, teamName) ->
            Box {
                PubFilterOptionChip(
                    label = teamName,
                    isSelected = teamId in selectedTeamIds,
                    onToggle = { onToggleTeam(teamId) },
                )
                if (teamId in favoriteTeamIds) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_team_favorite),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopStart),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegionFilterContent(
    selectedRegion: String?,
    onSelectRegion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        REGIONS.forEach { region ->
            PubFilterSubRegionChip(
                label = region,
                isSelected = region == selectedRegion,
                onClick = { onSelectRegion(region) },
            )
        }
    }
}

@Preview(showBackground = true, name = "팀 탭 — 응원구단 아이콘 + 선택 상태")
@Composable
private fun FilterSheetTeamPreview() {
    MoballTheme {
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = MoballTheme.colors.backgroundBase,
        ) {
            FilterBottomSheetContent(
                currentTab = FilterBottomSheetTab.TEAM,
                userFavoriteTeamIds = listOf(3L, 8L),
                selectedTeamIds = listOf(3L),
                selectedRegion = null,
                onTabChange = {},
                onToggleTeam = {},
                onSelectRegion = {},
                onApply = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "지역 탭 — 선택 상태")
@Composable
private fun FilterSheetRegionPreview() {
    MoballTheme {
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = MoballTheme.colors.backgroundBase,
        ) {
            FilterBottomSheetContent(
                currentTab = FilterBottomSheetTab.REGION,
                userFavoriteTeamIds = emptyList(),
                selectedTeamIds = emptyList(),
                selectedRegion = "잠실/송파",
                onTabChange = {},
                onToggleTeam = {},
                onSelectRegion = {},
                onApply = {},
            )
        }
    }
}
