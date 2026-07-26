package org.app.presentation.home.pubfilter

import androidx.lifecycle.SavedStateHandle
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

                // 홈에서 넘어온 현재 적용 필터로 최초 1회 선택 복원(이미 선택이 있으면 유지)
                is PubFilterContract.Event.OnSeed ->
                    setState { if (selectedOptions.isEmpty()) copy(selectedOptions = event.selected) else this }

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

/** 홈 → 필터 화면 진입 시 현재 적용된 필터를 넘기는 savedStateHandle 키. 단일 출처. */
internal object PubFilterSeedKeys {
    const val TEAM_IDS = "seed_team_ids"
    const val REGIONS = "seed_regions"
    const val OPEN_NOW = "seed_open_now"
    const val BUSINESS_DAY = "seed_business_day"
    const val FACILITY_CODES = "seed_facility_codes"
    const val STYLE_CODES = "seed_style_codes"
    const val THEME_CODES = "seed_theme_codes"
    const val FOOD_CODES = "seed_food_codes"
}

/**
 * 진입 시 넘어온 시드(현재 적용된 필터)를 필터 화면의 selectedOptions 로 복원한다.
 * 시드가 없으면(직접 진입 등) 빈 맵을 반환한다.
 */
internal fun seededSelection(handle: SavedStateHandle): Map<String, Set<String>> {
    val result = mutableMapOf<String, Set<String>>()

    handle.get<ArrayList<Long>>(PubFilterSeedKeys.TEAM_IDS)?.takeIf { it.isNotEmpty() }?.let { ids ->
        result[FilterSectionId.TEAM] = ids.map { it.toString() }.toSet()
    }
    handle.get<ArrayList<String>>(PubFilterSeedKeys.REGIONS)?.takeIf { it.isNotEmpty() }?.let {
        result[FilterSectionId.REGION] = it.toSet()
    }
    if (handle.get<Boolean>(PubFilterSeedKeys.OPEN_NOW) == true) {
        result[FilterSectionId.BUSINESS] = setOf("open")
    }
    BusinessDayFilter.optionIdOf(handle.get<String>(PubFilterSeedKeys.BUSINESS_DAY))?.let {
        result[FilterSectionId.BUSINESS_DAY] = setOf(it)
    }
    handle.get<ArrayList<String>>(PubFilterSeedKeys.FACILITY_CODES)?.takeIf { it.isNotEmpty() }?.let {
        result[FilterSectionId.STYLE_FACILITY] = it.toSet()
    }
    handle.get<ArrayList<String>>(PubFilterSeedKeys.STYLE_CODES)?.takeIf { it.isNotEmpty() }?.let {
        result[FilterSectionId.STYLE_BROADCAST] = it.toSet()
    }
    handle.get<ArrayList<String>>(PubFilterSeedKeys.THEME_CODES)?.takeIf { it.isNotEmpty() }?.let {
        result[FilterSectionId.STYLE_THEME] = it.toSet()
    }
    handle.get<ArrayList<String>>(PubFilterSeedKeys.FOOD_CODES)?.takeIf { it.isNotEmpty() }?.let {
        result[FilterSectionId.FOOD] = it.toSet()
    }
    return result
}
