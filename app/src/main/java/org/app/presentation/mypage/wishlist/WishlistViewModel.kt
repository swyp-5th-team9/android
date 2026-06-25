package org.app.presentation.mypage.wishlist

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(WishlistContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<WishlistContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        // TODO: API 연결 후 찜 목록 로드
    }
