package org.app.presentation.schedule

sealed interface ScheduleContract {
    data object State

    sealed interface SideEffect
}
