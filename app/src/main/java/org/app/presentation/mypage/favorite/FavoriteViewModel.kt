package org.app.presentation.mypage.favorite

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.FavoriteRepository
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel
    @Inject
    constructor(
        private val favoriteRepository: FavoriteRepository,
    ) : BaseViewModel<FavoriteContract.State, FavoriteContract.Event, FavoriteContract.SideEffect>(
            FavoriteContract.State(),
        ) {
        private val pendingHeartIds = mutableSetOf<Long>()
        private var firstResumePending = true

        init {
            loadFavorites()
        }

        override fun onEvent(event: FavoriteContract.Event) {
            when (event) {
                FavoriteContract.Event.OnRefresh -> {
                    refresh()
                }

                FavoriteContract.Event.OnEditClick -> {
                    setState { copy(isEditMode = !isEditMode, selectedIds = persistentSetOf()) }
                }

                FavoriteContract.Event.OnCancelEdit -> {
                    setState { copy(isEditMode = false, selectedIds = persistentSetOf()) }
                }

                FavoriteContract.Event.OnDeleteSelected -> {
                    val favoriteIds = currentState.selectedIds.toList()
                    if (favoriteIds.isEmpty()) return
                    viewModelScope.launch {
                        setState { copy(isLoading = true) }
                        favoriteRepository
                            .deleteFavorites(favoriteIds)
                            .onSuccess {
                                setState {
                                    copy(
                                        items = items.filter { it.favoriteId !in favoriteIds }.toImmutableList(),
                                        selectedIds = persistentSetOf(),
                                        isEditMode = false,
                                        isLoading = false,
                                    )
                                }
                                postSideEffect(
                                    FavoriteContract.SideEffect.ShowToast("${favoriteIds.size}개의 펍이 삭제되었습니다."),
                                )
                            }.onFailure { e ->
                                setState { copy(isLoading = false) }
                                postSideEffect(FavoriteContract.SideEffect.ShowToast(e.message ?: "삭제에 실패했습니다."))
                            }
                    }
                }

                is FavoriteContract.Event.OnToggleSelect -> {
                    setState {
                        val ids = selectedIds.toMutableSet()
                        if (event.favoriteId in ids) ids.remove(event.favoriteId) else ids.add(event.favoriteId)
                        copy(selectedIds = ids.toImmutableSet())
                    }
                }

                is FavoriteContract.Event.OnHeartClick -> {
                    if (currentState.isEditMode) return
                    if (!pendingHeartIds.add(event.favoriteId)) return
                    viewModelScope.launch {
                        try {
                            favoriteRepository
                                .deleteFavorites(listOf(event.favoriteId))
                                .onSuccess {
                                    setState {
                                        copy(
                                            items = items
                                                .filter {
                                                    it.favoriteId != event.favoriteId
                                                }.toImmutableList(),
                                        )
                                    }
                                    postSideEffect(
                                        FavoriteContract.SideEffect.ShowToast("1개의 펍이 삭제되었습니다."),
                                    )
                                }.onFailure { e ->
                                    postSideEffect(FavoriteContract.SideEffect.ShowToast(e.message ?: "삭제에 실패했습니다."))
                                }
                        } finally {
                            pendingHeartIds.remove(event.favoriteId)
                        }
                    }
                }

                is FavoriteContract.Event.OnPubClick -> {
                    if (currentState.isEditMode) {
                        val favoriteId = currentState.items
                            .find { it.pubId == event.pubId }
                            ?.favoriteId ?: return
                        onEvent(FavoriteContract.Event.OnToggleSelect(favoriteId))
                    } else {
                        postSideEffect(
                            FavoriteContract.SideEffect.NavigateToPubDetail(event.pubId.toString()),
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
                                items = favoriteItems
                                    .map { item ->
                                        FavoritePubItem(
                                            favoriteId = item.favoriteId,
                                            pubId = item.pubId,
                                            pubName = item.pubName,
                                            address = item.address,
                                            thumbnailImageUrl = item.thumbnailImageUrl,
                                        )
                                    }.toImmutableList(),
                            )
                        }
                    }.onFailure { e ->
                        setState { copy(isLoading = false) }
                        postSideEffect(FavoriteContract.SideEffect.ShowToast(e.message ?: "목록을 불러오지 못했습니다."))
                    }
            }
        }
    }
