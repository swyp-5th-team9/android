package org.app.data.remote.datasource.impl

import com.moball.app.BuildConfig
import org.app.data.mapper.toGameSchedule
import org.app.data.remote.datasource.api.MatchRemoteDataSource
import org.app.data.remote.dto.MatchItemResponse
import org.app.data.remote.dto.MatchTeamResponse
import org.app.data.remote.service.MatchService
import org.app.presentation.schedule.model.GameSchedule
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MatchRemoteDataSourceImpl
    @Inject
    constructor(
        private val matchService: MatchService,
    ) : MatchRemoteDataSource {
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        override suspend fun getMatches(
            sportType: String?,
            date: String?,
            from: String?,
            to: String?,
            teamId: Long?,
        ): List<GameSchedule> {
            if (BuildConfig.USE_MOCK_SERVER) {
                return generateMockMatches(from, to, date)
            }
            return matchService
                .getMatches(
                    sportType = sportType,
                    date = date,
                    from = from,
                    to = to,
                    teamId = teamId,
                ).data
                ?.matches
                ?.map { it.toGameSchedule() }
                ?: throw Exception("Failed to get matches: data is null")
        }

        /** mock 데이터 — API 연결 후 USE_MOCK_SERVER=false 시 사용 안 함 */
        private fun generateMockMatches(
            from: String?,
            to: String?,
            date: String?,
        ): List<GameSchedule> {
            val targetDate = when {
                date != null -> LocalDate.parse(date, dateFormatter)
                from != null -> LocalDate.parse(from, dateFormatter)
                else -> LocalDate.now()
            }
            val yearMonth = YearMonth.from(targetDate)
            val today = LocalDate.now()

            val mockItems = listOf(
                MatchItemResponse(
                    matchId = 1L,
                    sportType = "KBO",
                    matchDate = today.format(dateFormatter),
                    startTime = "18:30",
                    stadium = "잠실야구장",
                    status = "SCHEDULED",
                    homeTeam = MatchTeamResponse(6L, "두산", "두산 베어스"),
                    awayTeam = MatchTeamResponse(3L, "LG", "LG 트윈스"),
                ),
                MatchItemResponse(
                    matchId = 2L,
                    sportType = "KBO",
                    matchDate = today.format(dateFormatter),
                    startTime = "18:30",
                    stadium = "광주-기아 챔피언스 필드",
                    status = "SCHEDULED",
                    homeTeam = MatchTeamResponse(1L, "KIA", "KIA 타이거즈"),
                    awayTeam = MatchTeamResponse(8L, "삼성", "삼성 라이온즈"),
                ),
                MatchItemResponse(
                    matchId = 3L,
                    sportType = "KBO",
                    matchDate = today.format(dateFormatter),
                    startTime = "18:30",
                    stadium = "사직야구장",
                    status = "CANCELED",
                    homeTeam = MatchTeamResponse(7L, "롯데", "롯데 자이언츠"),
                    awayTeam = MatchTeamResponse(10L, "키움", "키움 히어로즈"),
                ),
                MatchItemResponse(
                    matchId = 4L,
                    sportType = "KBO",
                    matchDate = today.plusDays(2).format(dateFormatter),
                    startTime = "17:00",
                    stadium = "고척스카이돔",
                    status = "SCHEDULED",
                    homeTeam = MatchTeamResponse(9L, "한화", "한화 이글스"),
                    awayTeam = MatchTeamResponse(4L, "SSG", "SSG 랜더스"),
                ),
                MatchItemResponse(
                    matchId = 5L,
                    sportType = "KBO",
                    matchDate = today.plusDays(5).format(dateFormatter),
                    startTime = "18:30",
                    stadium = "인천SSG랜더스필드",
                    status = "SCHEDULED",
                    homeTeam = MatchTeamResponse(4L, "SSG", "SSG 랜더스"),
                    awayTeam = MatchTeamResponse(2L, "KT", "KT 위즈"),
                ),
            )
            return mockItems
                .map { it.toGameSchedule() }
                .filter { it.date.year == yearMonth.year && it.date.month == yearMonth.month }
        }
    }
