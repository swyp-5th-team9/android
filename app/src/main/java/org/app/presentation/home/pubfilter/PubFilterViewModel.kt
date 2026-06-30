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
import org.app.data.repository.api.TeamRepository
import org.app.presentation.home.model.PubFilterOption
import org.app.presentation.home.model.PubFilterSection
import javax.inject.Inject

@HiltViewModel
class PubFilterViewModel
    @Inject
    constructor(
        private val teamRepository: TeamRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(PubFilterContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<PubFilterContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadTeams()
        }

        private fun loadTeams() {
            viewModelScope.launch {
                teamRepository
                    .getTeams(sportType = "KBO")
                    .onSuccess { teams ->
                        _state.update { current ->
                            val teamSection = PubFilterSection(
                                sectionId = "team",
                                title = "응원팀",
                                options = listOf(PubFilterOption("all", "KBO 전체")) +
                                    teams.map {
                                        PubFilterOption(
                                            it.teamId.toString(),
                                            it.shortName,
                                        )
                                    },
                            )
                            current.copy(
                                teams = teams,
                                sections = current.sections.map { s ->
                                    if (s.sectionId == "team") teamSection else s
                                },
                            )
                        }
                    }.onFailure {
                        emit(PubFilterContract.SideEffect.ShowToast("팀 목록을 불러오는데 실패했습니다."))
                    }
            }
        }

        fun onEvent(event: PubFilterContract.Event) {
            when (event) {
                PubFilterContract.Event.OnBack ->
                    emit(PubFilterContract.SideEffect.NavigateBack)

                PubFilterContract.Event.OnReset ->
                    _state.update { it.copy(selectedOptions = emptyMap()) }

                PubFilterContract.Event.OnApply -> {
                    val state = _state.value
                    val selectedOptions = state.selectedOptions
                    val selectedTeamOptionIds = selectedOptions["team"] ?: emptySet()

                    if (state.teams.isEmpty() &&
                        "all" !in selectedTeamOptionIds &&
                        selectedTeamOptionIds.isNotEmpty()
                    ) {
                        emit(PubFilterContract.SideEffect.ShowToast("팀 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요."))
                        return
                    }
                    val teamIds: List<Long> = when {
                        "all" in selectedTeamOptionIds -> emptyList()
                        else -> selectedTeamOptionIds.mapNotNull { it.toLongOrNull() }
                    }
                    val teamNames: List<String> = when {
                        "all" in selectedTeamOptionIds -> listOf("KBO 전체")
                        else ->
                            state.teams
                                .filter { it.teamId.toString() in selectedTeamOptionIds }
                                .map { it.shortName }
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
                    val businessDayOptionId =
                        (selectedOptions["business_day"] ?: emptySet()).firstOrNull { it != "all_days" }
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

                    "region", "business_day" ->
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
