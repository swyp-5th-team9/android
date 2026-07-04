package org.app.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import org.app.core.network.AuthManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val authManager: AuthManager,
    ) : ViewModel() {
        val authEvent: SharedFlow<Unit> = authManager.authEvent
    }
