package org.app.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.presentation.schedule.model.GameSchedule
import org.app.presentation.schedule.model.GameStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel
    @Inject
    constructor(
        // TODO: ScheduleRepository 주입 (API 구현 후)
    ) : ViewModel() {
        private val _state = MutableStateFlow(ScheduleContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<ScheduleContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadGames(_state.value.currentMonth)
        }

        fun onEvent(event: ScheduleContract.Event) {
            when (event) {
                is ScheduleContract.Event.OnDateSelected -> {
                    val prevMonth = _state.value.currentMonth
                    _state.update { it.copy(selectedDate = event.date) }
                    val newMonth = YearMonth.from(event.date)
                    if (newMonth != prevMonth) loadGames(newMonth)
                }

                ScheduleContract.Event.OnPrevWeekClick -> {
                    val prev = _state.value
                    val newDate = prev.selectedDate.minusWeeks(1)
                    _state.update { it.copy(selectedDate = newDate) }
                    val newMonth = YearMonth.from(newDate)
                    if (newMonth != prev.currentMonth) loadGames(newMonth)
                }

                ScheduleContract.Event.OnNextWeekClick -> {
                    val prev = _state.value
                    val newDate = prev.selectedDate.plusWeeks(1)
                    _state.update { it.copy(selectedDate = newDate) }
                    val newMonth = YearMonth.from(newDate)
                    if (newMonth != prev.currentMonth) loadGames(newMonth)
                }

                ScheduleContract.Event.OnCalendarClick -> {
                    _state.update { it.copy(showCalendarDialog = true) }
                }

                is ScheduleContract.Event.OnDatePicked -> {
                    _state.update {
                        it.copy(
                            selectedDate = event.date,
                            showCalendarDialog = false,
                        )
                    }
                    loadGames(YearMonth.from(event.date))
                }

                ScheduleContract.Event.OnCalendarDismiss -> {
                    _state.update { it.copy(showCalendarDialog = false) }
                }

                is ScheduleContract.Event.OnTeamSelected -> {
                    _state.update { it.copy(selectedTeamId = event.teamId) }
                }
            }
        }

        private fun loadGames(yearMonth: YearMonth) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                // TODO: ScheduleRepository.getGames(yearMonth) 호출
                val mockGames = generateMockGames(yearMonth)
                _state.update { it.copy(games = mockGames, isLoading = false) }
            }
        }

        /** Mock 데이터 — API 연결 후 제거 */
        private fun generateMockGames(yearMonth: YearMonth): List<GameSchedule> {
            val today = LocalDate.now()
            return listOf(
                GameSchedule(
                    gameId = "1",
                    date = yearMonth.atDay(1).let { if (it.isAfter(today.minusDays(1))) it else today },
                    startTime = LocalTime.of(18, 30),
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
                    startTime = LocalTime.of(18, 30),
                    homeTeamId = 1,
                    homeTeamName = "기아 타이거즈",
                    awayTeamId = 8,
                    awayTeamName = "삼성 라이온즈",
                    stadium = "광주-기아 챔피언스 필드",
                    status = GameStatus.SCHEDULED,
                ),
                GameSchedule(
                    gameId = "3",
                    date = today,
                    startTime = LocalTime.of(18, 30),
                    homeTeamId = 7,
                    homeTeamName = "롯데 자이언츠",
                    awayTeamId = 10,
                    awayTeamName = "한화 이글스",
                    stadium = "사직야구장",
                    status = GameStatus.CANCELLED_RAIN,
                ),
                GameSchedule(
                    gameId = "4",
                    date = today.plusDays(2),
                    startTime = LocalTime.of(17, 0),
                    homeTeamId = 9,
                    homeTeamName = "키움 히어로즈",
                    awayTeamId = 4,
                    awayTeamName = "NC 다이노스",
                    stadium = "고척스카이돔",
                    status = GameStatus.SCHEDULED,
                ),
                GameSchedule(
                    gameId = "5",
                    date = today.plusDays(5),
                    startTime = LocalTime.of(18, 30),
                    homeTeamId = 5,
                    homeTeamName = "SSG 랜더스",
                    awayTeamId = 2,
                    awayTeamName = "KT WIZ",
                    stadium = "인천SSG랜더스필드",
                    status = GameStatus.SCHEDULED,
                ),
            ).filter { it.date.year == yearMonth.year && it.date.month == yearMonth.month }
        }
    }
