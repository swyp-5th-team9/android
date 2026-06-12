package org.app.presentation.schedule

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel
    @Inject
    constructor() : ViewModel() {
        private val _sideEffect = MutableSharedFlow<ScheduleContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()
    }
