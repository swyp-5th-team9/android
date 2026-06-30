package org.app.data.remote.datasource.impl

import com.moball.app.BuildConfig
import org.app.data.mapper.toTeamItem
import org.app.data.model.TeamItem
import org.app.data.remote.datasource.api.TeamRemoteDataSource
import org.app.data.remote.dto.TeamItemResponse
import org.app.data.remote.service.TeamService
import javax.inject.Inject

class TeamRemoteDataSourceImpl
    @Inject
    constructor(
        private val teamService: TeamService,
    ) : TeamRemoteDataSource {
        /** KBO V2 시드 기준 mock 데이터 */
        private val mockKboTeams = listOf(
            TeamItemResponse(1L, "LG 트윈스", "LG", "KBO", "서울 잠실야구장"),
            TeamItemResponse(2L, "두산 베어스", "두산", "KBO", "서울 잠실야구장"),
            TeamItemResponse(3L, "KT 위즈", "KT", "KBO", "수원 KT위즈파크"),
            TeamItemResponse(4L, "SSG 랜더스", "SSG", "KBO", "인천 SSG랜더스필드"),
            TeamItemResponse(5L, "NC 다이노스", "NC", "KBO", "창원 NC파크"),
            TeamItemResponse(6L, "KIA 타이거즈", "KIA", "KBO", "광주-기아 챔피언스 필드"),
            TeamItemResponse(7L, "롯데 자이언츠", "롯데", "KBO", "부산 사직야구장"),
            TeamItemResponse(8L, "삼성 라이온즈", "삼성", "KBO", "대구 삼성라이온즈파크"),
            TeamItemResponse(9L, "한화 이글스", "한화", "KBO", "대전 한화생명 볼파크"),
            TeamItemResponse(10L, "키움 히어로즈", "키움", "KBO", "고척 스카이돔"),
        )

        override suspend fun getTeams(sportType: String?): List<TeamItem> {
            if (BuildConfig.USE_MOCK_SERVER) {
                val filtered = if (sportType == null || sportType == "KBO") {
                    mockKboTeams
                } else {
                    emptyList()
                }
                return filtered.map { it.toTeamItem() }
            }
            return teamService
                .getTeams(sportType)
                .data
                ?.teams
                ?.map { it.toTeamItem() }
                ?: throw Exception("Failed to get teams: data is null")
        }
    }
