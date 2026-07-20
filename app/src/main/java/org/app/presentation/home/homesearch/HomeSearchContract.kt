package org.app.presentation.home.homesearch

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.app.presentation.home.model.PubSearchResult

interface HomeSearchContract {
    data class State(
        val query: String = "",
        val results: ImmutableList<PubSearchResult> = persistentListOf(),
        val isLoading: Boolean = false,
        val isEmpty: Boolean = false,
    )

    sealed interface Event {
        data object OnBack : Event

        data class OnQueryChange(
            val query: String,
        ) : Event

        data class OnResultClick(
            val pubId: String,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        data class NavigateToPubDetail(
            val pubId: String,
        ) : SideEffect
    }
}
