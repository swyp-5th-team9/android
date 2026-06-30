package org.app.presentation.schedule.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * 경기 상태
 * API 연결 시 서버 응답 string과 매핑 필요
 */
enum class GameStatus {
    SCHEDULED, // 예정
    IN_PROGRESS, // 진행 중
    FINISHED, // 종료
    CANCELLED_RAIN, // 우천 취소
    POSTPONED, // 연기
}

/**
 * 경기 일정 모델
 * TODO: API 연결 시 서버 응답 DTO → 이 모델로 매핑
 *
 * @param gameId       경기 고유 ID
 * @param date         경기 날짜
 * @param startTime    시작 시간 (null = 미정)
 * @param homeTeamId   홈팀 ID
 * @param homeTeamName 홈팀 이름 (예: "두산 베어스")
 * @param awayTeamId   어웨이팀 ID
 * @param awayTeamName 어웨이팀 이름
 * @param stadium      구장명 (예: "잠실야구장")
 * @param status       경기 상태
 * @param homeScore    홈팀 점수 (종료 후에만 존재)
 * @param awayScore    어웨이팀 점수 (종료 후에만 존재)
 */
data class GameSchedule(
    val gameId: String,
    val date: LocalDate,
    val startTime: LocalTime?,
    val homeTeamId: Long,
    val homeTeamName: String,
    val awayTeamId: Long,
    val awayTeamName: String,
    val stadium: String,
    val status: GameStatus = GameStatus.SCHEDULED,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
) {
    val isCancelled: Boolean get() =
        status == GameStatus.CANCELLED_RAIN || status == GameStatus.POSTPONED

    val isFinished: Boolean get() = status == GameStatus.FINISHED
}
