package org.app.presentation.home.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.domain.model.KboTeamType
import org.app.presentation.home.FilterBottomSheetTab
import org.app.presentation.home.model.SeoulRegion
import org.app.presentation.home.pubfilter.CITIES
import org.app.presentation.home.pubfilter.component.CityChip
import org.app.presentation.home.pubfilter.component.PubFilterOptionChip
import org.app.presentation.home.pubfilter.component.PubFilterSubRegionChip

/** 지역 필터 목록. 단일 출처는 [SeoulRegion]. */
private val REGIONS: List<Pair<String, String>> =
    SeoulRegion.entries.map { it.code to it.label }

/** 0L = 전체(KBO 전체) 의사 항목 + KboTeamType의 실제 10개 구단 (id 순서) */
private val KBO_TEAMS: List<Pair<Long, String>> =
    listOf(0L to "KBO 전체") + KboTeamType.teams.map { it.id.toLong() to it.shortName }

/**
 * 홈 화면 전용 필터 바텀시트
 * 응원팀 / 지역 두 탭 — 선택 후 "적용하기" 버튼
 *
 * @param initialTab          처음 표시할 탭
 * @param userFavoriteTeamIds 온보딩에서 선택한 응원 구단 ID (ic_team_favorite 표시)
 * @param initialTeamIds      현재 필터에 적용된 구단 ID
 * @param initialRegions      현재 필터에 적용된 지역 목록
 * @param onApply             (teamIds, teamNames, regions) 콜백
 * @param onDismiss           닫기 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFilterBottomSheet(
    initialTab: FilterBottomSheetTab,
    userFavoriteTeamIds: List<Long>,
    initialTeamIds: List<Long>,
    initialRegions: List<String>,
    onApply: (teamIds: List<Long>, teamNames: List<String>, regions: List<String>) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    // initial* 값이 바뀌면 편집 상태를 재초기화하도록 key를 명시한다.
    val selectedTeamIds = remember(initialTeamIds) { mutableStateListOf<Long>().also { it.addAll(initialTeamIds) } }
    val selectedRegions = remember(initialRegions) { mutableStateListOf<String>().also { it.addAll(initialRegions) } }
    var currentTab by remember(initialTab) { mutableStateOf(initialTab) }

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
            selectedRegions = selectedRegions,
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
            onToggleRegion = { regionCode ->
                val seoulAll = SeoulRegion.SEOUL_ALL.code
                when {
                    regionCode == seoulAll -> {
                        selectedRegions.clear()
                        selectedRegions.add(seoulAll)
                    }

                    regionCode in selectedRegions -> {
                        selectedRegions.remove(regionCode)
                    }

                    else -> {
                        selectedRegions.remove(seoulAll)
                        selectedRegions.add(regionCode)
                    }
                }
            },
            onApply = {
                val names = KBO_TEAMS
                    .filter { (id, _) -> id in selectedTeamIds }
                    .map { (_, name) -> name }
                onApply(selectedTeamIds.toList(), names, selectedRegions.toList())
            },
        )
    }
}

@Composable
private fun FilterBottomSheetContent(
    currentTab: FilterBottomSheetTab,
    userFavoriteTeamIds: List<Long>,
    selectedTeamIds: List<Long>,
    selectedRegions: List<String>,
    onTabChange: (FilterBottomSheetTab) -> Unit,
    onToggleTeam: (Long) -> Unit,
    onToggleRegion: (String) -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxSheetHeight = (LocalConfiguration.current.screenHeightDp * 0.8f).dp
    Column(
        modifier = modifier
            .heightIn(max = maxSheetHeight)
            // 시트 하단 "적용하기" 버튼이 시스템 내비게이션 바(하단바)와 겹치지 않도록 하단 인셋 확보
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            FilterBottomSheetTab.entries.forEach { tab ->
                val label = if (tab == FilterBottomSheetTab.TEAM) "응원팀" else "지역"
                val isActive = tab == currentTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp)
                        .noRippleClickable { onTabChange(tab) },
                ) {
                    Text(
                        text = label,
                        style = MoballTheme.typography.heading6.bold16,
                        color = if (isActive) {
                            MoballTheme.colors.textPrimary
                        } else {
                            MoballTheme.colors.textTertiary
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 2.dp),
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                if (isActive) MoballTheme.colors.accentPrimary else MoballTheme.colors.borderNormal,
                            ),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
        ) {
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
                        selectedRegions = selectedRegions,
                        onToggleRegion = onToggleRegion,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
            }

            Spacer(Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 25.dp),
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
    selectedRegions: List<String>,
    onToggleRegion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CITIES.forEach { city ->
                CityChip(
                    label = city.label,
                    isSelected = city.id == "seoul",
                    isEnabled = city.isEnabled,
                    onClick = { /* MVP: 서울만 지원 */ },
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MoballTheme.colors.backgroundSurface)
                .padding(16.dp),
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                REGIONS.forEach { (code, label) ->
                    PubFilterSubRegionChip(
                        label = label,
                        isSelected = code in selectedRegions,
                        onClick = { onToggleRegion(code) },
                    )
                }
            }
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
                selectedRegions = emptyList(),
                onTabChange = {},
                onToggleTeam = {},
                onToggleRegion = {},
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
                selectedRegions = listOf("JAMSIL"),
                onTabChange = {},
                onToggleTeam = {},
                onToggleRegion = {},
                onApply = {},
            )
        }
    }
}
