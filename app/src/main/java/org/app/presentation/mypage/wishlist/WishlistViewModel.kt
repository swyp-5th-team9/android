package org.app.presentation.mypage.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(WishlistContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<WishlistContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun onEvent(event: WishlistContract.Event) {
            when (event) {
                WishlistContract.Event.OnEditClick -> {
                    _state.update { it.copy(isEditMode = true, selectedIds = emptySet()) }
                }

                WishlistContract.Event.OnCancelEdit -> {
                    _state.update { it.copy(isEditMode = false, selectedIds = emptySet()) }
                }

                WishlistContract.Event.OnDeleteSelected -> {
                    val toRemove = _state.value.selectedIds
                    _state.update { current ->
                        current.copy(
                            items = current.items.filter { it.pubId !in toRemove },
                            selectedIds = emptySet(),
                            isEditMode = false,
                        )
                    }
                    // TODO: API 연결 - 서버에서 찜 삭제
                }

                is WishlistContract.Event.OnToggleSelect -> {
                    _state.update { current ->
                        val ids = current.selectedIds.toMutableSet()
                        if (event.pubId in ids) ids.remove(event.pubId) else ids.add(event.pubId)
                        current.copy(selectedIds = ids)
                    }
                }

                is WishlistContract.Event.OnToggleFavorite -> {
                    // 하트 취소 → 목록에서 제거
                    _state.update { current ->
                        current.copy(items = current.items.filter { it.pubId != event.pubId })
                    }
                    // TODO: API 연결 - 찜 해제
                }

                is WishlistContract.Event.OnPubClick -> {
                    // 편집 모드일 때는 선택 토글, 일반 모드에서는 상세 이동
                    if (_state.value.isEditMode) {
                        onEvent(WishlistContract.Event.OnToggleSelect(event.pubId))
                    } else {
                        viewModelScope.launch {
                            _sideEffect.emit(WishlistContract.SideEffect.NavigateToPubDetail(event.pubId))
                        }
                    }
                }
            }
        }
    }
