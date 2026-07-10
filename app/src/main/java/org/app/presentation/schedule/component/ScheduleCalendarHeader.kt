package org.app.presentation.schedule.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private fun DayOfWeek.toKorean(): String =
    when (this) {
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
        DayOfWeek.SUNDAY -> "일"
    }

/**
 * 경기일정 캘린더 헤더
 * - 월 이동 (< >) : 이전/다음 주 (-7일 / +7일)
 * - 캘린더 아이콘 : 탭 시 캘린더 모달 오픈
 * - 주간 7일 스트립 : selectedDate ±3일
 */
@Composable
fun ScheduleCalendarHeader(
    currentMonth: YearMonth,
    weekDates: List<LocalDate>,
    selectedDate: LocalDate,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCalendarClick: () -> Unit,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            // 헤더 하단 드롭섀도우 — 피그마(elevation 20/4%)는 너무 옅고 기본 그림자는 너무 진해 중간값으로 조정
            // (elevation = 퍼짐, 색상 알파 0x26 ≈ 15% = 진하기)
            .shadow(
                elevation = 20.dp,
                spotColor = Color(0x26000000),
                ambientColor = Color(0x26000000),
            ).background(MoballTheme.colors.backgroundBase),
    ) {
        MonthNavigationRow(
            currentMonth = currentMonth,
            onPrevWeek = onPrevWeek,
            onNextWeek = onNextWeek,
            onCalendarClick = onCalendarClick,
        )

        Spacer(modifier = Modifier.height(8.dp))

        DateStrip(
            weekDates = weekDates,
            selectedDate = selectedDate,
            onDateClick = onDateClick,
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun MonthNavigationRow(
    currentMonth: YearMonth,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(87.dp))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_left_md),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable(onClick = onPrevWeek),
        )

        Spacer(Modifier.width(40.dp))

        Text(
            text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
            style = MoballTheme.typography.heading5.bold18,
            color = MoballTheme.colors.textPrimary,
        )

        Spacer(Modifier.width(36.dp))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_right_md),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable(onClick = onNextWeek),
        )

        Spacer(Modifier.width(34.dp))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_calender),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(48.dp)
                .noRippleClickable(onClick = onCalendarClick),
        )
    }
}

@Composable
private fun DateStrip(
    weekDates: List<LocalDate>,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    Row(modifier = modifier.fillMaxWidth()) {
        weekDates.forEach { date ->
            DateItem(
                date = date,
                isToday = date == today,
                isSelected = date == selectedDate,
                onClick = { onDateClick(date) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DateItem(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dayLabelColor = when {
        isToday -> MoballTheme.colors.calendarTodayLabel
        else -> MoballTheme.colors.textTertiary
    }

    Column(
        modifier = modifier
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isToday) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MoballTheme.colors.calendarTodayDot),
            )
            Spacer(modifier = Modifier.height(2.dp))
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
        val dayLabel = if (isToday) "오늘" else date.dayOfWeek.toKorean()

        Text(
            text = dayLabel,
            style = MoballTheme.typography.body.medium14,
            color = dayLabelColor,
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isSelected -> Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MoballTheme.colors.textPrimary),
                )
            }

            Text(
                text = date.dayOfMonth.toString(),
                style = MoballTheme.typography.heading5.semibold18,
                color = when {
                    isSelected -> MoballTheme.colors.staticWhite
                    isToday -> MoballTheme.colors.calendarTodayDate
                    else -> MoballTheme.colors.textPrimary
                },
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleCalendarHeaderPreview() {
    MoballTheme {
        val today = LocalDate.now()
        ScheduleCalendarHeader(
            currentMonth = YearMonth.now(),
            weekDates = (-3..3).map { today.plusDays(it.toLong()) },
            selectedDate = today,
            onPrevWeek = {},
            onNextWeek = {},
            onCalendarClick = {},
            onDateClick = {},
        )
    }
}
