package org.app.presentation.home.pubfilter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.TeamRepository
import org.app.presentation.home.model.PubFilterOption
import org.app.presentation.home.model.PubFilterSection
import org.app.presentation.home.model.SeoulRegion
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
            val selectedTeamOptionIds = selectedOptions[FilterSectionId.TEAM] ?: emptySet()

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

            val selectedRegionIds = selectedOptions[FilterSectionId.REGION] ?: emptySet()
            // optionId = 백엔드 enum 코드이므로 그대로 전달
            // SEOUL_ALL(서울 전체) 선택 시 region 파라미터 없음(null → 전체 조회)
            val regions = when {
                SeoulRegion.SEOUL_ALL.code in selectedRegionIds || selectedRegionIds.isEmpty() -> emptyList()
                else -> selectedRegionIds.toList()
            }

            val openNow = if ("open" in (selectedOptions[FilterSectionId.BUSINESS] ?: emptySet())) true else null
            val businessDayOptionId =
                (selectedOptions[FilterSectionId.BUSINESS_DAY] ?: emptySet())
                    .firstOrNull { it != BusinessDayFilter.ALL_DAYS.optionId }
            val businessDay = BusinessDayFilter.serverCodeOf(businessDayOptionId)

            // 펍스타일 4종 섹션의 optionId는 서버 코드이므로 그대로 전달
            val facilityCodes = (selectedOptions[FilterSectionId.STYLE_FACILITY] ?: emptySet()).toList()
            val styleCodes = (selectedOptions[FilterSectionId.STYLE_BROADCAST] ?: emptySet()).toList()
            val themeCodes = (selectedOptions[FilterSectionId.STYLE_THEME] ?: emptySet()).toList()
            val foodCodes = (selectedOptions[FilterSectionId.FOOD] ?: emptySet()).toList()

            postSideEffect(
                PubFilterContract.SideEffect.ApplyFilter(
                    selectedOptions,
                    teamIds,
                    teamNames,
                    regions,
                    openNow,
                    businessDay,
                    facilityCodes,
                    styleCodes,
                    themeCodes,
                    foodCodes,
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
                                sectionId = FilterSectionId.TEAM,
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
                                teams = teams.toImmutableList(),
                                sections = sections
                                    .map { s ->
                                        if (s.sectionId == FilterSectionId.TEAM) teamSection else s
                                    }.toImmutableList(),
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
                val seoulAll = SeoulRegion.SEOUL_ALL.code
                val newSet = when (sectionId) {
                    FilterSectionId.TEAM -> when {
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

                    FilterSectionId.BUSINESS_DAY ->
                        if (optionId in currentSet) {
                            emptySet()
                        } else {
                            setOf(optionId)
                        }

                    FilterSectionId.BUSINESS ->
                        if (optionId in currentSet) emptySet() else setOf(optionId)

                    FilterSectionId.REGION -> {
                        when {
                            optionId == seoulAll ->
                                if (currentSet == setOf(seoulAll)) emptySet() else setOf(seoulAll)

                            else -> {
                                val baseSet = currentSet - seoulAll
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
