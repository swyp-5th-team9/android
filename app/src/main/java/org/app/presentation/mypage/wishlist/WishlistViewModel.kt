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
import org.app.data.repository.api.FavoriteRepository
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel
    @Inject
    constructor(
        private val favoriteRepository: FavoriteRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(WishlistContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<WishlistContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadFavorites()
        }

        fun refresh() {
            loadFavorites()
        }

        fun onEvent(event: WishlistContract.Event) {
            when (event) {
                WishlistContract.Event.OnEditClick -> {
                    _state.update { it.copy(isEditMode = !it.isEditMode, selectedIds = emptySet()) }
                }

                WishlistContract.Event.OnCancelEdit -> {
                    _state.update { it.copy(isEditMode = false, selectedIds = emptySet()) }
                }

                WishlistContract.Event.OnDeleteSelected -> {
                    val favoriteIds = _state.value.selectedIds.toList()
                    if (favoriteIds.isEmpty()) return
                    viewModelScope.launch {
                        _state.update { it.copy(isLoading = true) }
                        favoriteRepository
                            .deleteFavorites(favoriteIds)
                            .onSuccess {
                                _state.update { current ->
                                    current.copy(
                                        items = current.items.filter { it.favoriteId !in favoriteIds },
                                        selectedIds = emptySet(),
                                        isEditMode = false,
                                        isLoading = false,
                                    )
                                }
                                _sideEffect.emit(
                                    WishlistContract.SideEffect.ShowToast("${favoriteIds.size}개의 펍이 삭제되었습니다."),
                                )
                            }.onFailure { e ->
                                _state.update { it.copy(isLoading = false) }
                                _sideEffect.emit(WishlistContract.SideEffect.ShowToast(e.message ?: "삭제에 실패했습니다."))
                            }
                    }
                }

                is WishlistContract.Event.OnToggleSelect -> {
                    _state.update { current ->
                        val ids = current.selectedIds.toMutableSet()
                        if (event.favoriteId in ids) ids.remove(event.favoriteId) else ids.add(event.favoriteId)
                        current.copy(selectedIds = ids)
                    }
                }

                is WishlistContract.Event.OnPubClick -> {
                    if (_state.value.isEditMode) {
                        val favoriteId = _state.value.items
                            .find { it.pubId == event.pubId }
                            ?.favoriteId ?: return
                        onEvent(WishlistContract.Event.OnToggleSelect(favoriteId))
                    } else {
                        viewModelScope.launch {
                            _sideEffect.emit(
                                WishlistContract.SideEffect.NavigateToPubDetail(event.pubId.toString()),
                            )
                        }
                    }
                }
            }
        }

        private fun loadFavorites() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                favoriteRepository
                    .getFavorites()
                    .onSuccess { items ->
                        _state.update { current ->
                            current.copy(
                                isLoading = false,
                                items = items.map { item ->
                                    WishlistItem(
                                        favoriteId = item.favoriteId,
                                        pubId = item.pubId,
                                        pubName = item.pubName,
                                        address = item.address,
                                        thumbnailImageUrl = item.thumbnailImageUrl,
                                    )
                                },
                            )
                        }
                    }.onFailure { e ->
                        _state.update { it.copy(isLoading = false) }
                        _sideEffect.emit(WishlistContract.SideEffect.ShowToast(e.message ?: "목록을 불러오지 못했습니다."))
                    }
            }
        }
    }
