package org.app.presentation.onboarding.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
    @Inject
    constructor() : ViewModel() {
        private val _sideEffect = MutableSharedFlow<SignUpContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()
    }
