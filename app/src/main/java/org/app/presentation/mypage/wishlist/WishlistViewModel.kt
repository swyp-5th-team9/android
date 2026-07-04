package org.app.presentation.mypage.wishlist

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.FavoriteRepository
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel
    @Inject
    constructor(
        private val favoriteRepository: FavoriteRepository,
    ) : BaseViewModel<WishlistContract.State, WishlistContract.Event, WishlistContract.SideEffect>(
            WishlistContract.State(),
        ) {
        private val pendingHeartIds = mutableSetOf<Long>()
        private var firstResumePending = true

        init {
            loadFavorites()
        }

        override fun onEvent(event: WishlistContract.Event) {
            when (event) {
                WishlistContract.Event.OnRefresh -> {
                    refresh()
                }

                WishlistContract.Event.OnEditClick -> {
                    setState { copy(isEditMode = !isEditMode, selectedIds = emptySet()) }
                }

                WishlistContract.Event.OnCancelEdit -> {
                    setState { copy(isEditMode = false, selectedIds = emptySet()) }
                }

                WishlistContract.Event.OnDeleteSelected -> {
                    val favoriteIds = currentState.selectedIds.toList()
                    if (favoriteIds.isEmpty()) return
                    viewModelScope.launch {
                        setState { copy(isLoading = true) }
                        favoriteRepository
                            .deleteFavorites(favoriteIds)
                            .onSuccess {
                                setState {
                                    copy(
                                        items = items.filter { it.favoriteId !in favoriteIds },
                                        selectedIds = emptySet(),
                                        isEditMode = false,
                                        isLoading = false,
                                    )
                                }
                                postSideEffect(
                                    WishlistContract.SideEffect.ShowToast("${favoriteIds.size}개의 펍이 삭제되었습니다."),
                                )
                            }.onFailure { e ->
                                setState { copy(isLoading = false) }
                                postSideEffect(WishlistContract.SideEffect.ShowToast(e.message ?: "삭제에 실패했습니다."))
                            }
                    }
                }

                is WishlistContract.Event.OnToggleSelect -> {
                    setState {
                        val ids = selectedIds.toMutableSet()
                        if (event.favoriteId in ids) ids.remove(event.favoriteId) else ids.add(event.favoriteId)
                        copy(selectedIds = ids)
                    }
                }

                is WishlistContract.Event.OnHeartClick -> {
                    if (currentState.isEditMode) return
                    if (!pendingHeartIds.add(event.favoriteId)) return
                    viewModelScope.launch {
                        try {
                            favoriteRepository
                                .deleteFavorites(listOf(event.favoriteId))
                                .onSuccess {
                                    setState {
                                        copy(items = items.filter { it.favoriteId != event.favoriteId })
                                    }
                                }.onFailure { e ->
                                    postSideEffect(WishlistContract.SideEffect.ShowToast(e.message ?: "삭제에 실패했습니다."))
                                }
                        } finally {
                            pendingHeartIds.remove(event.favoriteId)
                        }
                    }
                }

                is WishlistContract.Event.OnPubClick -> {
                    if (currentState.isEditMode) {
                        val favoriteId = currentState.items
                            .find { it.pubId == event.pubId }
                            ?.favoriteId ?: return
                        onEvent(WishlistContract.Event.OnToggleSelect(favoriteId))
                    } else {
                        postSideEffect(
                            WishlistContract.SideEffect.NavigateToPubDetail(event.pubId.toString()),
                        )
                    }
                }
            }
        }

        private fun refresh() {
            if (firstResumePending) {
                firstResumePending = false
                return
            }
            loadFavorites()
        }

        private fun loadFavorites() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                favoriteRepository
                    .getFavorites()
                    .onSuccess { favoriteItems ->
                        setState {
                            copy(
                                isLoading = false,
                                items = favoriteItems.map { item ->
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
                        setState { copy(isLoading = false) }
                        postSideEffect(WishlistContract.SideEffect.ShowToast(e.message ?: "목록을 불러오지 못했습니다."))
                    }
            }
        }
    }
