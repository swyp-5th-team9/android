package org.app.presentation.schedule

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.MatchRepository
import org.app.data.repository.api.TeamRepository
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel
    @Inject
    constructor(
        private val teamRepository: TeamRepository,
        private val matchRepository: MatchRepository,
    ) : BaseViewModel<ScheduleContract.State, ScheduleContract.Event, ScheduleContract.SideEffect>(
            ScheduleContract.State(),
        ) {
        private var loadGamesJob: Job? = null

        init {
            loadTeams()
            loadGames(currentState.currentMonth)
        }

        override fun onEvent(event: ScheduleContract.Event) {
            when (event) {
                is ScheduleContract.Event.OnDateSelected -> {
                    val prevMonth = currentState.currentMonth
                    setState { copy(selectedDate = event.date) }
                    val newMonth = YearMonth.from(event.date)
                    if (newMonth != prevMonth) loadGames(newMonth)
                }

                ScheduleContract.Event.OnPrevWeekClick -> {
                    val prev = currentState
                    val newDate = prev.selectedDate.minusWeeks(1)
                    setState { copy(selectedDate = newDate) }
                    val newMonth = YearMonth.from(newDate)
                    if (newMonth != prev.currentMonth) loadGames(newMonth)
                }

                ScheduleContract.Event.OnNextWeekClick -> {
                    val prev = currentState
                    val newDate = prev.selectedDate.plusWeeks(1)
                    setState { copy(selectedDate = newDate) }
                    val newMonth = YearMonth.from(newDate)
                    if (newMonth != prev.currentMonth) loadGames(newMonth)
                }

                ScheduleContract.Event.OnCalendarClick -> {
                    setState { copy(showCalendarDialog = true) }
                }

                is ScheduleContract.Event.OnDatePicked -> {
                    setState {
                        copy(
                            selectedDate = event.date,
                            showCalendarDialog = false,
                        )
                    }
                    loadGames(YearMonth.from(event.date))
                }

                ScheduleContract.Event.OnCalendarDismiss -> {
                    setState { copy(showCalendarDialog = false) }
                }

                is ScheduleContract.Event.OnTeamSelected -> {
                    setState { copy(selectedTeamId = event.teamId) }
                }
            }
        }

        private fun loadTeams() {
            viewModelScope.launch {
                teamRepository
                    .getTeams(sportType = "KBO")
                    .onSuccess { teams ->
                        setState { copy(teams = teams) }
                    }.onFailure {
                        postSideEffect(ScheduleContract.SideEffect.ShowToast("팀 정보를 불러오는데 실패했습니다."))
                    }
            }
        }

        private fun loadGames(yearMonth: YearMonth) {
            loadGamesJob?.cancel()
            loadGamesJob = viewModelScope.launch {
                setState { copy(isLoading = true) }
                matchRepository
                    .getMatches(
                        sportType = "KBO",
                        from = yearMonth.atDay(1).toString(),
                        to = yearMonth.atEndOfMonth().toString(),
                    ).onSuccess { games ->
                        setState { copy(games = games, isLoading = false) }
                    }.onFailure {
                        setState { copy(isLoading = false) }
                        postSideEffect(ScheduleContract.SideEffect.ShowToast("경기 일정을 불러오는데 실패했습니다."))
                    }
            }
        }
    }
