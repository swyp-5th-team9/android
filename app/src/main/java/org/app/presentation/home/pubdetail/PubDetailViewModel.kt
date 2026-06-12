package org.app.presentation.home.pubdetail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class PubDetailViewModel
    @Inject
    constructor() : ViewModel() {
        private val _sideEffect = MutableSharedFlow<PubDetailContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()
    }
