package org.app.presentation.home.pubfilter

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import kotlinx.coroutines.launch
import org.app.core.common.util.CollectSideEffect
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.home.pubfilter.component.PubFilterRegionSectionContent
import org.app.presentation.home.pubfilter.component.PubFilterSection
import org.app.presentation.home.pubfilter.component.PubFilterTabRow

private val TABS = listOf("지역", "영업상태", "응원팀", "펍 스타일", "음식")

private fun tabIndexToItemIndex(tabIdx: Int) = tabIdx + 1

@Composable
fun PubFilterRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onApplyFilter: (
        teamIds: List<Long>,
        teamNames: List<String>,
        regions: List<String>,
        openNow: Boolean?,
        businessDay: String?,
        facilityCodes: List<String>,
        styleCodes: List<String>,
        themeCodes: List<String>,
        foodCodes: List<String>,
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    viewModel: PubFilterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CollectSideEffect(viewModel.sideEffect) { effect ->
        when (effect) {
            PubFilterContract.SideEffect.NavigateBack -> onBack()
            is PubFilterContract.SideEffect.ApplyFilter -> {
                onApplyFilter(
                    effect.teamIds,
                    effect.teamNames,
                    effect.regions,
                    effect.openNow,
                    effect.businessDay,
                    effect.facilityCodes,
                    effect.styleCodes,
                    effect.themeCodes,
                    effect.foodCodes,
                )
                onBack()
            }

            is PubFilterContract.SideEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    PubFilterScreen(state = state, onEvent = viewModel::onEvent, modifier = modifier)
}

@Composable
internal fun PubFilterScreen(
    state: PubFilterContract.State,
    onEvent: (PubFilterContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val activeTabIndex by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf 0

            if (!lazyListState.canScrollForward) return@derivedStateOf TABS.lastIndex

            val firstVisibleIndex = lazyListState.firstVisibleItemIndex
            (firstVisibleIndex - 1).coerceIn(0, TABS.lastIndex)
        }
    }

    val showTabBorder by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 ||
                lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    val sectionMap = remember(state.sections) { state.sections.associateBy { it.sectionId } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(
            state = TopBarState.Back(
                title = "필터",
                onBackClick = { onEvent(PubFilterContract.Event.OnBack) },
            ),
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f),
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MoballTheme.colors.backgroundBase),
                ) {
                    PubFilterTabRow(
                        tabs = TABS,
                        activeIndex = activeTabIndex,
                        onTabClick = { idx ->
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(tabIndexToItemIndex(idx))
                            }
                        },
                    )
                    if (showTabBorder) {
                        HorizontalDivider(color = MoballTheme.colors.borderNormal, thickness = 1.dp)
                    }
                }
            }

            item {
                PubFilterRegionSectionContent(
                    selectedSubRegionIds = state.selectedOptions["region"] ?: emptySet(),
                    onToggleSubRegion = { id ->
                        onEvent(PubFilterContract.Event.OnOptionToggle("region", id))
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 24.dp),
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    Text(
                        text = "영업",
                        style = MoballTheme.typography.heading3.bold20,
                        color = MoballTheme.colors.textPrimary,
                    )

                    sectionMap[FilterSectionId.BUSINESS]?.let { s ->
                        PubFilterSection(
                            section = s,
                            selectedOptionIds = state.selectedOptions[s.sectionId] ?: emptySet(),
                            onToggle = { id -> onEvent(PubFilterContract.Event.OnOptionToggle(s.sectionId, id)) },
                        )
                    }
                    sectionMap[FilterSectionId.BUSINESS_DAY]?.let { s ->
                        PubFilterSection(
                            section = s,
                            selectedOptionIds = state.selectedOptions[s.sectionId] ?: emptySet(),
                            onToggle = { id -> onEvent(PubFilterContract.Event.OnOptionToggle(s.sectionId, id)) },
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = "응원팀",
                        style = MoballTheme.typography.heading3.bold20,
                        color = MoballTheme.colors.textPrimary,
                    )
                    sectionMap[FilterSectionId.TEAM]?.let { s ->
                        PubFilterSection(
                            section = s,
                            selectedOptionIds = state.selectedOptions[s.sectionId] ?: emptySet(),
                            onToggle = { id -> onEvent(PubFilterContract.Event.OnOptionToggle(s.sectionId, id)) },
                            modifier = Modifier.padding(bottom = 40.dp),
                            showTitle = false,
                        )
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    Text(
                        text = "펍 스타일",
                        style = MoballTheme.typography.heading3.bold20,
                        color = MoballTheme.colors.textPrimary,
                    )

                    sectionMap[FilterSectionId.STYLE_BROADCAST]?.let { s ->
                        PubFilterSection(
                            section = s,
                            selectedOptionIds = state.selectedOptions[s.sectionId] ?: emptySet(),
                            onToggle = { id -> onEvent(PubFilterContract.Event.OnOptionToggle(s.sectionId, id)) },
                        )
                    }
                    sectionMap[FilterSectionId.STYLE_FACILITY]?.let { s ->
                        PubFilterSection(
                            section = s,
                            selectedOptionIds = state.selectedOptions[s.sectionId] ?: emptySet(),
                            onToggle = { id -> onEvent(PubFilterContract.Event.OnOptionToggle(s.sectionId, id)) },
                        )
                    }
                    sectionMap[FilterSectionId.STYLE_THEME]?.let { s ->
                        PubFilterSection(
                            section = s,
                            selectedOptionIds = state.selectedOptions[s.sectionId] ?: emptySet(),
                            onToggle = { id -> onEvent(PubFilterContract.Event.OnOptionToggle(s.sectionId, id)) },
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Text(
                        text = "음식 / 주류",
                        style = MoballTheme.typography.heading3.bold20,
                        color = MoballTheme.colors.textPrimary,
                    )
                    sectionMap[FilterSectionId.FOOD]?.let { s ->
                        PubFilterSection(
                            section = s,
                            selectedOptionIds = state.selectedOptions[s.sectionId] ?: emptySet(),
                            onToggle = { id -> onEvent(PubFilterContract.Event.OnOptionToggle(s.sectionId, id)) },
                            showTitle = false,
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MoballTheme.colors.borderNormal, thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoballTheme.colors.backgroundBase)
                .padding(horizontal = 16.dp, vertical = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MoballTheme.colors.backgroundBase)
                    .border(1.dp, MoballTheme.colors.borderNormal, RoundedCornerShape(12.dp))
                    .then(
                        if (state.hasSelection) {
                            Modifier.noRippleClickable {
                                onEvent(PubFilterContract.Event.OnReset)
                            }
                        } else {
                            Modifier
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }

            MoballButton(
                text = "적용하기",
                onClick = { onEvent(PubFilterContract.Event.OnApply) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun PubFilterScreenPreview() {
    MoballTheme {
        PubFilterScreen(
            state = PubFilterContract.State(
                selectedOptions = mapOf(
                    "region" to setOf("GANGNAM", "HONGDAE_HAPJEONG"),
                    "business" to setOf("open"),
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun PubFilterScreenEmptyPreview() {
    MoballTheme {
        PubFilterScreen(state = PubFilterContract.State(), onEvent = {})
    }
}
