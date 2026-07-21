package org.app.presentation.schedule

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.app.data.model.TeamItem
import org.app.presentation.schedule.model.GameSchedule
import java.time.LocalDate
import java.time.YearMonth

interface ScheduleContract {
    data class State(
        /** GET /api/v1/teams 응답 — 팀 칩바에 사용 */
        val teams: ImmutableList<TeamItem> = persistentListOf(),
        val selectedDate: LocalDate = LocalDate.now(),
        val games: ImmutableList<GameSchedule> = persistentListOf(),
        val isLoading: Boolean = false,
        val showCalendarDialog: Boolean = false,
        val selectedTeamId: Long? = null, // null = 전체
    ) {
        /** 현재 표시 중인 달 (selectedDate 기준) */
        val currentMonth: YearMonth
            get() = YearMonth.from(selectedDate)

        /** 주간 뷰에 표시할 7일 (selectedDate ±3일, 가운데 배치) */
        val weekDates: List<LocalDate>
            get() = (-3..3).map { selectedDate.plusDays(it.toLong()) }

        /** 날짜 → 게임 매핑 */
        val gamesByDate: Map<LocalDate, List<GameSchedule>>
            get() = games.groupBy { it.date }

        /** 선택된 날짜의 게임 목록 (팀 필터 적용) */
        val selectedDateGames: List<GameSchedule>
            get() {
                val all = gamesByDate[selectedDate] ?: emptyList()
                return if (selectedTeamId == null) {
                    all
                } else {
                    all.filter { it.homeTeamId == selectedTeamId || it.awayTeamId == selectedTeamId }
                }
            }
    }

    sealed interface Event {
        data class OnDateSelected(
            val date: LocalDate,
        ) : Event

        data object OnPrevWeekClick : Event

        data object OnNextWeekClick : Event

        data object OnCalendarClick : Event

        data class OnDatePicked(
            val date: LocalDate,
        ) : Event

        data object OnCalendarDismiss : Event

        data class OnTeamSelected(
            val teamId: Long?,
        ) : Event
    }

    sealed interface SideEffect {
        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
