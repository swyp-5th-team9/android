package org.app.presentation.home.homesearch

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.app.core.analytics.AnalyticsEvent
import org.app.core.analytics.AnalyticsHelper
import org.app.core.analytics.AnalyticsParam
import org.app.core.common.base.BaseViewModel
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
        private val analyticsHelper: AnalyticsHelper,
    ) : BaseViewModel<HomeSearchContract.State, HomeSearchContract.Event, HomeSearchContract.SideEffect>(
            HomeSearchContract.State(),
        ) {
        private val queryFlow = MutableStateFlow("")

        init {
            viewModelScope.launch {
                queryFlow
                    .debounce(300L)
                    .distinctUntilChanged()
                    .collect { query -> search(query) }
            }
        }

        override fun onEvent(event: HomeSearchContract.Event) {
            when (event) {
                HomeSearchContract.Event.OnBack ->
                    postSideEffect(HomeSearchContract.SideEffect.NavigateBack)

                is HomeSearchContract.Event.OnQueryChange -> {
                    setState { copy(query = event.query, isEmpty = false) }
                    queryFlow.value = event.query
                }

                is HomeSearchContract.Event.OnResultClick ->
                    postSideEffect(HomeSearchContract.SideEffect.NavigateToPubDetail(event.pubId))
            }
        }

        private fun search(query: String) {
            if (query.isBlank()) {
                setState { copy(results = persistentListOf(), isEmpty = false, isLoading = false) }
                return
            }
            viewModelScope.launch {
                setState { copy(isLoading = true) }
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
                        setState {
                            copy(isLoading = false, results = results.toImmutableList(), isEmpty = results.isEmpty())
                        }
                        analyticsHelper.logEvent(
                            AnalyticsEvent.MAP_SEARCH,
                            mapOf(AnalyticsParam.RESULT_COUNT to results.size),
                        )
                    }.onFailure { error ->
                        Timber.e("검색 실패: $error")
                        setState { copy(isLoading = false, results = persistentListOf(), isEmpty = true) }
                    }
            }
        }
    }
