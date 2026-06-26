package org.app.presentation.schedule

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.schedule.component.ScheduleCalendarDialog
import org.app.presentation.schedule.component.ScheduleCalendarHeader
import org.app.presentation.schedule.component.ScheduleGameItem
import org.app.presentation.schedule.component.ScheduleTeamChipBar
import org.app.presentation.schedule.model.GameSchedule
import org.app.presentation.schedule.model.GameStatus
import java.time.LocalDate

@Composable
fun ScheduleRoute(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is ScheduleContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ScheduleScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
internal fun ScheduleScreen(
    state: ScheduleContract.State,
    onEvent: (ScheduleContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(state = TopBarState.Default(title = "경기일정"))

        ScheduleCalendarHeader(
            currentMonth = state.currentMonth,
            weekDates = state.weekDates,
            selectedDate = state.selectedDate,
            onPrevWeek = { onEvent(ScheduleContract.Event.OnPrevWeekClick) },
            onNextWeek = { onEvent(ScheduleContract.Event.OnNextWeekClick) },
            onCalendarClick = { onEvent(ScheduleContract.Event.OnCalendarClick) },
            onDateClick = { date -> onEvent(ScheduleContract.Event.OnDateSelected(date)) },
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "우천 취소 · 연기는 실시간 반영이 어려울 수 있어요.\n방문 전 KBO · 구단 공지를 확인해 주세요.",
                    style = MoballTheme.typography.caption.medium12,
                    color = MoballTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                ScheduleTeamChipBar(
                    selectedTeamId = state.selectedTeamId,
                    onTeamSelected = { teamId ->
                        onEvent(ScheduleContract.Event.OnTeamSelected(teamId))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (state.selectedDateGames.isEmpty()) {
                item {
                    EmptyGameState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                    )
                }
            } else {
                items(
                    items = state.selectedDateGames,
                    key = { it.gameId },
                ) { game ->
                    ScheduleGameItem(
                        game = game,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (state.showCalendarDialog) {
        ScheduleCalendarDialog(
            initialDate = state.selectedDate,
            onConfirm = { date ->
                onEvent(ScheduleContract.Event.OnDatePicked(date))
            },
            onDismiss = { onEvent(ScheduleContract.Event.OnCalendarDismiss) },
        )
    }
}

@Composable
private fun EmptyGameState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = "이날은 경기가 없어요",
            style = MoballTheme.typography.body.medium14,
            color = MoballTheme.colors.textTertiary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleScreenWithGamesPreview() {
    MoballTheme {
        val today = LocalDate.now()
        ScheduleScreen(
            state = ScheduleContract.State(
                selectedDate = today,
                games = listOf(
                    GameSchedule(
                        gameId = "1",
                        date = today,
                        startTime = java.time.LocalTime.of(18, 30),
                        homeTeamId = 6,
                        homeTeamName = "두산 베어스",
                        awayTeamId = 3,
                        awayTeamName = "LG 트윈스",
                        stadium = "잠실야구장",
                        status = GameStatus.SCHEDULED,
                    ),
                    GameSchedule(
                        gameId = "2",
                        date = today,
                        startTime = java.time.LocalTime.of(18, 30),
                        homeTeamId = 7,
                        homeTeamName = "롯데 자이언츠",
                        awayTeamId = 10,
                        awayTeamName = "한화 이글스",
                        stadium = "사직야구장",
                        status = GameStatus.CANCELLED_RAIN,
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleScreenEmptyPreview() {
    MoballTheme {
        ScheduleScreen(
            state = ScheduleContract.State(selectedDate = LocalDate.now()),
            onEvent = {},
        )
    }
}
