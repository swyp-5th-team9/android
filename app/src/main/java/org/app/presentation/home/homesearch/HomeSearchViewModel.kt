package org.app.presentation.home.homesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.data.repository.api.PubRepository
import org.app.presentation.home.model.PubSearchResult
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeSearchViewModel
    @Inject
    constructor(
        private val pubRepository: PubRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(HomeSearchContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<HomeSearchContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        private val queryFlow = MutableStateFlow("")

        init {
            viewModelScope.launch {
                queryFlow
                    .debounce(300L)
                    .distinctUntilChanged()
                    .collect { query -> search(query) }
            }
        }

        fun onEvent(event: HomeSearchContract.Event) {
            when (event) {
                HomeSearchContract.Event.OnBack ->
                    emit(HomeSearchContract.SideEffect.NavigateBack)

                is HomeSearchContract.Event.OnQueryChange -> {
                    _state.update { it.copy(query = event.query, isEmpty = false) }
                    queryFlow.value = event.query
                }

                is HomeSearchContract.Event.OnResultClick ->
                    emit(HomeSearchContract.SideEffect.NavigateToPubDetail(event.pubId))
            }
        }

        private fun search(query: String) {
            if (query.isBlank()) {
                _state.update { it.copy(results = emptyList(), isEmpty = false, isLoading = false) }
                return
            }
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                pubRepository
                    .getPubs(keyword = query, size = 20)
                    .onSuccess { page ->
                        val results = page.content.map { item ->
                            PubSearchResult(
                                pubId = item.pubId.toString(),
                                name = item.name,
                                address = item.address,
                            )
                        }
                        _state.update {
                            it.copy(isLoading = false, results = results, isEmpty = results.isEmpty())
                        }
                    }.onFailure { error ->
                        Timber.e("검색 실패: $error")
                        _state.update { it.copy(isLoading = false, results = emptyList(), isEmpty = true) }
                    }
            }
        }

        private fun emit(effect: HomeSearchContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
