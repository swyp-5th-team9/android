package org.app.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    ) : ViewModel() {
        private val _state = MutableStateFlow(ScheduleContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<ScheduleContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadTeams()
            loadGames(_state.value.currentMonth)
        }

        private fun loadTeams() {
            viewModelScope.launch {
                teamRepository
                    .getTeams(sportType = "KBO")
                    .onSuccess { teams ->
                        _state.update { it.copy(teams = teams) }
                    }.onFailure {
                        emit(ScheduleContract.SideEffect.ShowToast("팀 정보를 불러오는데 실패했습니다."))
                    }
            }
        }

        private fun emit(effect: ScheduleContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
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

        private var loadGamesJob: Job? = null

        private fun loadGames(yearMonth: YearMonth) {
            loadGamesJob?.cancel()
            loadGamesJob = viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                matchRepository
                    .getMatches(
                        sportType = "KBO",
                        from = yearMonth.atDay(1).toString(),
                        to = yearMonth.atEndOfMonth().toString(),
                    ).onSuccess { games ->
                        _state.update { it.copy(games = games, isLoading = false) }
                    }.onFailure {
                        _state.update { it.copy(isLoading = false) }
                        emit(ScheduleContract.SideEffect.ShowToast("경기 일정을 불러오는데 실패했습니다."))
                    }
            }
        }
    }
