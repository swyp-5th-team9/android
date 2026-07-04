package org.app.presentation.home.pubfilter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.TeamRepository
import org.app.presentation.home.model.PubFilterOption
import org.app.presentation.home.model.PubFilterSection
import javax.inject.Inject

@HiltViewModel
class PubFilterViewModel
    @Inject
    constructor(
        private val teamRepository: TeamRepository,
    ) : BaseViewModel<PubFilterContract.State, PubFilterContract.Event, PubFilterContract.SideEffect>(
            PubFilterContract.State(),
        ) {
        init {
            loadTeams()
        }

        override fun onEvent(event: PubFilterContract.Event) {
            when (event) {
                PubFilterContract.Event.OnBack ->
                    postSideEffect(PubFilterContract.SideEffect.NavigateBack)

                PubFilterContract.Event.OnReset ->
                    setState { copy(selectedOptions = emptyMap()) }

                PubFilterContract.Event.OnApply -> applyFilter()

                is PubFilterContract.Event.OnOptionToggle -> toggleOption(event.sectionId, event.optionId)
            }
        }

        private fun applyFilter() {
            val state = currentState
            val selectedOptions = state.selectedOptions
            val selectedTeamOptionIds = selectedOptions["team"] ?: emptySet()

            if (state.teams.isEmpty() &&
                "all" !in selectedTeamOptionIds &&
                selectedTeamOptionIds.isNotEmpty()
            ) {
                postSideEffect(PubFilterContract.SideEffect.ShowToast("팀 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요."))
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

            val selectedRegionIds = selectedOptions["region"] ?: emptySet()
            // optionId = 백엔드 enum 코드이므로 그대로 전달
            // SEOUL_ALL(서울 전체) 선택 시 region 파라미터 없음(null → 전체 조회)
            val regions = when {
                "SEOUL_ALL" in selectedRegionIds || selectedRegionIds.isEmpty() -> emptyList()
                else -> selectedRegionIds.toList()
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

            postSideEffect(
                PubFilterContract.SideEffect.ApplyFilter(
                    selectedOptions,
                    teamIds,
                    teamNames,
                    regions,
                    openNow,
                    businessDay,
                ),
            )
        }

        private fun loadTeams() {
            viewModelScope.launch {
                teamRepository
                    .getTeams(sportType = "KBO")
                    .onSuccess { teams ->
                        setState {
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
                            copy(
                                teams = teams,
                                sections = sections.map { s ->
                                    if (s.sectionId == "team") teamSection else s
                                },
                            )
                        }
                    }.onFailure {
                        postSideEffect(PubFilterContract.SideEffect.ShowToast("팀 목록을 불러오는데 실패했습니다."))
                    }
            }
        }

        private fun toggleOption(
            sectionId: String,
            optionId: String,
        ) {
            setState {
                val currentSet = selectedOptions[sectionId] ?: emptySet()
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

                    "business_day" ->
                        if (optionId in currentSet) {
                            emptySet()
                        } else {
                            setOf(optionId)
                        }

                    "region" -> {
                        when {
                            optionId == "SEOUL_ALL" ->
                                if (currentSet == setOf("SEOUL_ALL")) emptySet() else setOf("SEOUL_ALL")

                            else -> {
                                val baseSet = currentSet - "SEOUL_ALL"
                                if (optionId in baseSet) baseSet - optionId else baseSet + optionId
                            }
                        }
                    }

                    else ->
                        if (optionId in currentSet) currentSet - optionId else currentSet + optionId
                }

                copy(selectedOptions = selectedOptions + (sectionId to newSet))
            }
        }
    }
