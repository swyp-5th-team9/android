package org.app.data.mapper

import org.app.core.util.TimeUtils
import org.app.data.remote.dto.MatchItemResponse
import org.app.presentation.schedule.model.GameSchedule
import org.app.presentation.schedule.model.GameStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun MatchItemResponse.toGameSchedule(): GameSchedule? {
    val finalDate = runCatching { LocalDate.parse(matchDate, DATE_FORMATTER) }.getOrNull() ?: return null
    val finalTime = TimeUtils.parseUtcToKst(startTime)

    return GameSchedule(
        gameId = matchId.toString(),
        date = finalDate,
        startTime = finalTime,
        homeTeamId = homeTeam.teamId,
        homeTeamName = homeTeam.name,
        homeTeamShortName = homeTeam.shortName,
        awayTeamId = awayTeam.teamId,
        awayTeamName = awayTeam.name,
        awayTeamShortName = awayTeam.shortName,
        stadium = stadium,
        status = when (status) {
            "SCHEDULED" -> GameStatus.SCHEDULED
            "LIVE" -> GameStatus.IN_PROGRESS
            "FINISHED" -> GameStatus.FINISHED
            "CANCELED" -> GameStatus.CANCELLED_RAIN
            "POSTPONED" -> GameStatus.POSTPONED
            else -> GameStatus.SCHEDULED
        },
        homeScore = homeScore,
        awayScore = awayScore,
    )
}
