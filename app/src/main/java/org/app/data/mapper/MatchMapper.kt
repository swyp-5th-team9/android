package org.app.data.mapper

import org.app.data.remote.dto.MatchItemResponse
import org.app.presentation.schedule.model.GameSchedule
import org.app.presentation.schedule.model.GameStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

fun MatchItemResponse.toGameSchedule(): GameSchedule =
    GameSchedule(
        gameId = matchId.toString(),
        date = runCatching { LocalDate.parse(matchDate, DATE_FORMATTER) }
            .getOrElse { LocalDate.now() },
        startTime = startTime?.let {
            runCatching { LocalTime.parse(it, TIME_FORMATTER) }.getOrNull()
        },
        homeTeamId = homeTeam.teamId,
        homeTeamName = homeTeam.name,
        awayTeamId = awayTeam.teamId,
        awayTeamName = awayTeam.name,
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
