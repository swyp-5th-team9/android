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
import kotlin.collections.emptySet

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
                    val selectedTeamOptionIds = selectedOptions["team"] ?: emptySet()

                    /** 백엔드 V2 시드 기준 */
                    val teamIdMap = mapOf(
                        "all" to 0,
                        "lg" to 1,
                        "doosan" to 2,
                        "kt" to 3,
                        "ssg" to 4,
                        "nc" to 5,
                        "kia" to 6,
                        "lotte" to 7,
                        "samsung" to 8,
                        "hanwha" to 9,
                        "kiwoom" to 10,
                    )
                    val teamIds = when {
                        "all" in selectedTeamOptionIds -> listOf(0)
                        else -> selectedTeamOptionIds.mapNotNull { teamIdMap[it] }
                    }
                    val teamNames = when {
                        "all" in selectedTeamOptionIds -> listOf("KBO 전체")
                        else ->
                            teamSection
                                ?.options
                                ?.filter { it.id in selectedTeamOptionIds }
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
                    val openNow = if ("open" in (selectedOptions["business"] ?: emptySet())) true else null
                    val businessDayOptionId = (selectedOptions["business_day"] ?: emptySet())
                        .firstOrNull { it != "all_days" }
                    val businessDay = when (businessDayOptionId) {
                        "weekdays" -> "WEEKDAY"
                        "weekends" -> "WEEKEND"
                        "always_open" -> "EVERYDAY"
                        "mon" -> "MON"
                        "tue" -> "TUE"
                        "wed" -> "WED"
                        "thu" -> "THU"
                        "fri" -> "FRI"
                        "sat" -> "SAT"
                        "sun" -> "SUN"
                        else -> null
                    }
                    emit(
                        PubFilterContract.SideEffect.ApplyFilter(
                            selectedOptions,
                            teamIds,
                            teamNames,
                            region,
                            openNow,
                            businessDay,
                        ),
                    )
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
                val newSet = when (sectionId) {
                    "team" -> when {
                        optionId == "all" ->
                            if (currentSet == setOf("all")) {
                                emptySet()
                            } else {
                                setOf("all")
                            }

                        else -> {
                            val baseSet = currentSet - "all"
                            if (optionId in baseSet) baseSet - optionId else baseSet + optionId
                        }
                    }

                    "region" ->
                        if (optionId in currentSet) {
                            emptySet()
                        } else {
                            setOf(optionId)
                        }

                    else ->
                        if (optionId in currentSet) currentSet - optionId else currentSet + optionId
                }

                current.copy(
                    selectedOptions = current.selectedOptions + (sectionId to newSet),
                )
            }
        }

        private fun emit(effect: PubFilterContract.SideEffect) {
            viewModelScope.launch { _sideEffect.emit(effect) }
        }
    }
