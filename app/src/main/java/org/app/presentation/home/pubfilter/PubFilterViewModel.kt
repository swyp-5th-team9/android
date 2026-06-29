package org.app.presentation.home.pubfilter

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
class PubFilterViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(PubFilterContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<PubFilterContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun onEvent(event: PubFilterContract.Event) {
            when (event) {
                PubFilterContract.Event.OnBack ->
                    emit(PubFilterContract.SideEffect.NavigateBack)

                PubFilterContract.Event.OnReset ->
                    _state.update { it.copy(selectedOptions = emptyMap()) }

                PubFilterContract.Event.OnApply -> {
                    val state = _state.value
                    val selectedOptions = state.selectedOptions
                    val teamSection = state.sections.find { it.sectionId == "team" }
                    val selectedTeamIds = selectedOptions["team"] ?: emptySet()
                    val teamNames = when {
                        "all" in selectedTeamIds -> listOf("KBO 전체")
                        else ->
                            teamSection
                                ?.options
                                ?.filter { it.id in selectedTeamIds }
                                ?.map { it.label }
                                ?: emptyList()
                    }
                    val regionSection = state.sections.find { it.sectionId == "region" }
                    val selectedRegionIds = selectedOptions["region"] ?: emptySet()
                    val region = when {
                        "seoul_all" in selectedRegionIds -> "서울 전체"
                        else ->
                            regionSection
                                ?.options
                                ?.firstOrNull { it.id in selectedRegionIds }
                                ?.label
                    }
                    emit(PubFilterContract.SideEffect.ApplyFilter(selectedOptions, teamNames, region))
                }

                is PubFilterContract.Event.OnOptionToggle -> toggleOption(event.sectionId, event.optionId)
            }
        }

        private fun toggleOption(
            sectionId: String,
            optionId: String,
        ) {
            _state.update { current ->
                val currentSet = current.selectedOptions[sectionId] ?: emptySet()
                val newSet = if (optionId in currentSet) currentSet - optionId else currentSet + optionId
                current.copy(
                    selectedOptions = current.selectedOptions + (sectionId to newSet),
                )
            }
        }

        private fun emit(effect: PubFilterContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
