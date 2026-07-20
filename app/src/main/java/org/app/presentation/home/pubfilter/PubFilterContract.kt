package org.app.presentation.home.pubfilter

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.app.data.model.TeamItem
import org.app.domain.model.KboTeamType
import org.app.presentation.home.model.PubFilterOption
import org.app.presentation.home.model.PubFilterSection
import org.app.presentation.home.model.SeoulRegion

interface PubFilterContract {
    data class State(
        /** GET /api/v1/teams 응답 — 로드 후 team 섹션을 동적으로 구성 */
        val teams: ImmutableList<TeamItem> = persistentListOf(),
        val sections: ImmutableList<PubFilterSection> = defaultSections(),
        /** sectionId → Set<optionId> */
        val selectedOptions: Map<String, Set<String>> = emptyMap(),
        val isLoading: Boolean = false,
    ) {
        val hasSelection: Boolean
            get() = selectedOptions.values.any { it.isNotEmpty() }

        /** teams 로드 후 team 섹션 옵션을 동적으로 교체한 섹션 목록 */
        val resolvedSections: ImmutableList<PubFilterSection>
            get() {
                if (teams.isEmpty()) return sections
                val teamSection = PubFilterSection(
                    sectionId = FilterSectionId.TEAM,
                    title = "응원팀",
                    options = listOf(PubFilterOption("all", "KBO 전체")) +
                        teams.map { PubFilterOption(it.teamId.toString(), it.shortName) },
                )
                return sections.map { if (it.sectionId == FilterSectionId.TEAM) teamSection else it }.toImmutableList()
            }
    }

    sealed interface Event {
        data object OnBack : Event

        data object OnReset : Event

        data object OnApply : Event

        data class OnOptionToggle(
            val sectionId: String,
            val optionId: String,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        data class ApplyFilter(
            val selected: Map<String, Set<String>>,
            val teamIds: List<Long>,
            val teamNames: List<String>,
            val regions: List<String>,
            val openNow: Boolean?,
            val businessDay: String?,
            val facilityCodes: List<String>,
            val styleCodes: List<String>,
            val themeCodes: List<String>,
            val foodCodes: List<String>,
        ) : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}

/**
 * 지역 필터 옵션. 단일 출처는 [SeoulRegion].
 * optionId = 백엔드 region 코드 (SEOUL_ALL → 전체)
 */
internal val SEOUL_SUB_REGIONS: List<PubFilterOption> =
    SeoulRegion.entries.map { PubFilterOption(it.code, it.label) }

internal data class CityOption(
    val id: String,
    val label: String,
    val isEnabled: Boolean,
)

internal val CITIES: List<CityOption> = listOf(
    CityOption("seoul", "서울", isEnabled = true),
    CityOption("gyeonggi", "경기", isEnabled = false),
    CityOption("incheon", "인천", isEnabled = false),
    CityOption("busan", "부산", isEnabled = false),
)

private fun defaultSections(): ImmutableList<PubFilterSection> =
    persistentListOf(
        PubFilterSection(
            sectionId = FilterSectionId.REGION,
            title = "지역",
            options = SEOUL_SUB_REGIONS,
        ),
        PubFilterSection(
            sectionId = FilterSectionId.BUSINESS,
            title = "영업상태",
            options = listOf(
                PubFilterOption("open", "영업 중"),
                PubFilterOption("all", "전체"),
            ),
        ),
        PubFilterSection(
            sectionId = FilterSectionId.BUSINESS_DAY,
            title = "영업 요일",
            options = BusinessDayFilter.options,
        ),
        // teams 로드 전 placeholder — 로드 후 State.resolvedSections가 서버 teamId로 교체
        PubFilterSection(
            sectionId = FilterSectionId.TEAM,
            title = "응원팀",
            options = listOf(PubFilterOption("all", "KBO 전체")) +
                KboTeamType.teams.map { PubFilterOption(it.id.toString(), it.shortName) },
        ),
        // optionId = 서버 화이트리스트 코드(그대로 styleCodes 파라미터로 전달)
        PubFilterSection(
            sectionId = FilterSectionId.STYLE_BROADCAST,
            title = "경기 상영 스타일",
            options = BroadcastStyleCode.entries.toFilterOptions(),
        ),
        // optionId = 서버 화이트리스트 코드(그대로 facilityCodes 파라미터로 전달)
        PubFilterSection(
            sectionId = FilterSectionId.STYLE_FACILITY,
            title = "시설 / 서비스",
            options = FacilityCode.entries.toFilterOptions(),
        ),
        // optionId = 서버 화이트리스트 코드(그대로 themeCodes 파라미터로 전달)
        PubFilterSection(
            sectionId = FilterSectionId.STYLE_THEME,
            title = "테마",
            options = ThemeCode.entries.toFilterOptions(),
        ),
        // optionId = 서버 화이트리스트 코드(그대로 foodCodes 파라미터로 전달)
        PubFilterSection(
            sectionId = FilterSectionId.FOOD,
            title = "음식 / 주류",
            options = FoodCode.entries.toFilterOptions(),
        ),
    )
