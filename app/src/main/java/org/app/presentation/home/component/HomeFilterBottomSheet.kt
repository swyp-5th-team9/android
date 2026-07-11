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
import org.app.presentation.home.FilterBottomSheetTab
import org.app.presentation.home.pubfilter.CITIES
import org.app.presentation.home.pubfilter.component.CityChip
import org.app.presentation.home.pubfilter.component.PubFilterOptionChip
import org.app.presentation.home.pubfilter.component.PubFilterSubRegionChip

/** 백엔드 region 코드 to 한글 표시 라벨 */
private val REGIONS: List<Pair<String, String>> = listOf(
    "SEOUL_ALL" to "서울 전체",
    "GANGNAM" to "강남/서초",
    "JAMSIL" to "잠실/잠실새내",
    "SONGPA" to "송파",
    "GANGDONG" to "강동/천호",
    "SEONGDONG" to "성수/왕십리",
    "JUNGNANG" to "중랑",
    "JONGNO" to "종로",
    "JUNGGU" to "중구",
    "HONGDAE_HAPJEONG" to "홍대/합정",
    "SANGAM_MANGWON" to "상암/망원",
    "MAPO" to "마포",
    "EUNPYEONG" to "은평/서대문",
    "YEONGDEUNGPO" to "영등포/여의도",
    "GANGSEO" to "마곡/강서",
    "DONGJAK" to "동작",
    "NOWON" to "노원/강북",
    "DOBONG" to "도봉/성북",
    "GURO" to "구로",
    "GWANAK" to "관악",
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
                when {
                    regionCode == "SEOUL_ALL" -> {
                        selectedRegions.clear()
                        selectedRegions.add("SEOUL_ALL")
                    }
                    regionCode in selectedRegions -> {
                        selectedRegions.remove(regionCode)
                    }
                    else -> {
                        selectedRegions.remove("SEOUL_ALL")
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
    // 세부 지역 칩(최대 20개)이 많은 기기에서 하단 버튼이 잘리지 않도록,
    // 시트 최대 높이를 화면의 90%로 제한하고 콘텐츠 영역만 스크롤한다.
    val maxSheetHeight = (LocalConfiguration.current.screenHeightDp * 0.9f).dp
    Column(modifier = modifier.heightIn(max = maxSheetHeight)) {
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

        // 콘텐츠가 화면을 넘칠 때만 이 영역이 스크롤되고, 버튼은 하단에 고정된다.
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
