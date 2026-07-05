package org.app.presentation.schedule.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.schedule.model.GameSchedule
import org.app.presentation.schedule.model.GameStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

/**
 * 팀 ID → 로고 drawable 매핑
 * API 연결 후 서버 teamId 기준으로 수정
 */
@DrawableRes
fun teamLogoRes(teamId: Long): Int =
    when (teamId.toInt()) {
        1 -> R.drawable.img_lg
        2 -> R.drawable.img_doosan
        3 -> R.drawable.img_kt
        4 -> R.drawable.img_ssg
        5 -> R.drawable.img_nc
        6 -> R.drawable.img_kia
        7 -> R.drawable.img_lotte
        8 -> R.drawable.img_samsung
        9 -> R.drawable.img_hanwha
        10 -> R.drawable.img_kiwoom
        else -> R.drawable.img_doosan
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
            TeamSection(
                teamLogoRes = teamLogoRes(game.homeTeamId),
                align = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.width(90.dp),
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
                            text = "${game.homeScore} : ${game.awayScore}",
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
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(10.dp))

            TeamSection(
                teamLogoRes = teamLogoRes(game.awayTeamId),
                align = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TeamSection(
    @DrawableRes teamLogoRes: Int,
    modifier: Modifier = Modifier,
    align: Alignment.Horizontal,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = align,
    ) {
        Image(
            painter = painterResource(teamLogoRes),
            contentDescription = null,
        )
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
                homeTeamName = "두산 베어스",
                awayTeamId = 3,
                awayTeamName = "LG 트윈스",
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
                awayTeamId = 10,
                awayTeamName = "한화 이글스",
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
                homeTeamId = 1,
                homeTeamName = "기아 타이거즈",
                awayTeamId = 8,
                awayTeamName = "삼성 라이온즈",
                stadium = "광주-기아 챔피언스 필드",
                status = GameStatus.FINISHED,
                homeScore = 5,
                awayScore = 3,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
