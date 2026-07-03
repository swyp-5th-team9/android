package org.app.data.mapper

import org.app.data.remote.dto.MatchItemResponse
import org.app.presentation.schedule.model.GameSchedule
import org.app.presentation.schedule.model.GameStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
private val KST_ZONE = ZoneId.of("Asia/Seoul")
private val UTC_ZONE = ZoneId.of("UTC")

fun MatchItemResponse.toGameSchedule(): GameSchedule {
    val rawDate = LocalDate.parse(matchDate, DATE_FORMATTER)
    val rawTime = startTime?.let {
        runCatching { LocalTime.parse(it.take(5), TIME_FORMATTER) }.getOrNull()
    }

    // 서버가 UTC로 내려주는 경우 현지 시간(KST)으로 변환
    val (finalDate, finalTime) = if (rawTime != null) {
        val zonedDateTime = LocalDateTime
            .of(rawDate, rawTime)
            .atZone(UTC_ZONE)
            .withZoneSameInstant(KST_ZONE)
        zonedDateTime.toLocalDate() to zonedDateTime.toLocalTime()
    } else {
        rawDate to null
    }

    return GameSchedule(
        gameId = matchId.toString(),
        date = finalDate,
        startTime = finalTime,
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
}
