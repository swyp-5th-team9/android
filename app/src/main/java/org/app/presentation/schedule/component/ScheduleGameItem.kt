package org.app.presentation.schedule.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.pubdetail.model.KboTeamType
import org.app.presentation.schedule.model.GameSchedule
import org.app.presentation.schedule.model.GameStatus
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

/**
 * 팀 ID → shortName → 팀명(부분 일치) 순으로 로고 drawable 매핑.
 * 전부 실패하면 진단 로그를 남기고 null(로고 없음)을 반환한다.
 */
@DrawableRes
fun teamLogoRes(
    teamId: Long,
    teamShortName: String,
    teamName: String = "",
): Int? {
    val matched = KboTeamType.matchOrNull(
        id = teamId.toInt(),
        shortName = teamShortName,
        fullName = teamName,
    )
    if (matched == null) {
        Timber.w(
            "팀 로고 매핑 실패 — teamId=%d, shortName='%s', name='%s' (서버 ID 체계/필드명 확인 필요)",
            teamId,
            teamShortName,
            teamName,
        )
        return null
    }
    return matched.logoRes
}

/**
 * 경기 일정 아이템 카드
 *
 * 상태에 따라:
 * - 예정/진행 중: 시작 시간 표시
 * - 종료: 최종 점수 표시
 * - 우천취소/연기: 취소 뱃지 표시
 */
@Composable
fun ScheduleGameItem(
    game: GameSchedule,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MoballTheme.colors.backgroundPage, RoundedCornerShape(16.dp))
            .padding(horizontal = 35.dp, vertical = 22.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val homeLogoRes = remember(game.homeTeamId, game.homeTeamShortName, game.homeTeamName) {
                teamLogoRes(game.homeTeamId, game.homeTeamShortName, game.homeTeamName)
            }
            TeamSection(
                teamName = game.homeTeamShortName,
                teamLogoRes = homeLogoRes,
                align = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            )

            Column(
                modifier = Modifier.weight(1.2f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when {
                    game.isCancelled -> {
                        CancelBadge(
                            label = if (game.status == GameStatus.CANCELLED_RAIN) "우천취소" else "연기",
                        )
                    }

                    game.isFinished && game.homeScore != null && game.awayScore != null -> {
                        Text(
                            text = "${game.homeScore}:${game.awayScore}",
                            style = MoballTheme.typography.heading5.bold18,
                            color = MoballTheme.colors.textPrimary,
                        )
                        Text(
                            text = "종료",
                            style = MoballTheme.typography.caption.medium12,
                            color = MoballTheme.colors.textTertiary,
                        )
                    }

                    else -> {
                        Text(
                            text = game.startTime?.format(TIME_FORMATTER) ?: "미정",
                            style = MoballTheme.typography.heading3.bold20,
                            color = MoballTheme.colors.textPrimary,
                        )
                        Text(
                            text = game.stadium,
                            style = MoballTheme.typography.caption.regular12,
                            color = MoballTheme.colors.textPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }
                }
            }

            val awayLogoRes = remember(game.awayTeamId, game.awayTeamShortName, game.awayTeamName) {
                teamLogoRes(game.awayTeamId, game.awayTeamShortName, game.awayTeamName)
            }
            TeamSection(
                teamName = game.awayTeamShortName,
                teamLogoRes = awayLogoRes,
                align = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TeamSection(
    teamName: String,
    @DrawableRes teamLogoRes: Int?,
    modifier: Modifier = Modifier,
    align: Alignment.Horizontal,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = align,
    ) {
        if (teamLogoRes != null) {
            Image(
                painter = painterResource(teamLogoRes),
                contentDescription = null,
                modifier = Modifier.size(77.dp),
            )
        } else {
            // 로고 매핑 실패 시 팀명 텍스트로 대체
            Text(
                text = teamName,
                style = MoballTheme.typography.heading5.bold18,
                color = MoballTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CancelBadge(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(MoballTheme.colors.backgroundSurface, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MoballTheme.typography.caption.medium12,
            color = MoballTheme.colors.textTertiary,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F2F4)
@Composable
private fun ScheduleGameItemScheduledPreview() {
    MoballTheme {
        ScheduleGameItem(
            game = GameSchedule(
                gameId = "1",
                date = LocalDate.now(),
                startTime = LocalTime.of(18, 30),
                homeTeamId = 6,
                homeTeamName = "KIA 타이거즈",
                homeTeamShortName = "KIA",
                awayTeamId = 1,
                awayTeamName = "LG 트윈스",
                awayTeamShortName = "LG",
                stadium = "잠실야구장",
                status = GameStatus.SCHEDULED,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F2F4)
@Composable
private fun ScheduleGameItemCancelledPreview() {
    MoballTheme {
        ScheduleGameItem(
            game = GameSchedule(
                gameId = "2",
                date = LocalDate.now(),
                startTime = LocalTime.of(18, 30),
                homeTeamId = 7,
                homeTeamName = "롯데 자이언츠",
                homeTeamShortName = "롯데",
                awayTeamId = 9,
                awayTeamName = "한화 이글스",
                awayTeamShortName = "한화",
                stadium = "사직야구장",
                status = GameStatus.CANCELLED_RAIN,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F2F4)
@Composable
private fun ScheduleGameItemFinishedPreview() {
    MoballTheme {
        ScheduleGameItem(
            game = GameSchedule(
                gameId = "3",
                date = LocalDate.now(),
                startTime = LocalTime.of(18, 30),
                homeTeamId = 6,
                homeTeamName = "KIA 타이거즈",
                homeTeamShortName = "KIA",
                awayTeamId = 8,
                awayTeamName = "삼성 라이온즈",
                awayTeamShortName = "삼성",
                stadium = "광주-기아 챔피언스 필드",
                status = GameStatus.FINISHED,
                homeScore = 5,
                awayScore = 3,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
