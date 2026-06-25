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
        private val _state = MutableStateFlow(
            WishlistContract.State(
                items = listOf(
                    WishlistItem("1", "야구펍 홍대점", "마포구", isFavorite = true),
                    WishlistItem("2", "롯데 응원 맛집", "잠실동", isFavorite = false),
                    WishlistItem("3", "시그니처 펍", "용산구", isFavorite = true),
                    WishlistItem("4", "홈런 안주 맛집", "강남구", isFavorite = true),
                    WishlistItem("5", "베이스볼 파크", "영등포구", isFavorite = false),
                    WishlistItem("6", "스트라이크 존", "송파구", isFavorite = true),
                ),
            ),
        )
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
                    val count = toRemove.size
                    _state.update { current ->
                        current.copy(
                            items = current.items.filter { it.pubId !in toRemove },
                            selectedIds = emptySet(),
                            isEditMode = false,
                        )
                    }
                    viewModelScope.launch {
                        _sideEffect.emit(WishlistContract.SideEffect.ShowToast("${count}개의 펍이 삭제되었습니다."))
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
                    _state.update { current ->
                        current.copy(
                            items = current.items.map {
                                if (it.pubId == event.pubId) it.copy(isFavorite = !it.isFavorite) else it
                            },
                        )
                    }
                    // TODO: API 연결 - 찜 상태 업데이트
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
