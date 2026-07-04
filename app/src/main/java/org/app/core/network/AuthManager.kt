package org.app.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager
    @Inject
    constructor() {
        private val _authEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        val authEvent: SharedFlow<Unit> = _authEvent.asSharedFlow()

        suspend fun emitAuthEvent() {
            _authEvent.emit(Unit)
        }
    }
