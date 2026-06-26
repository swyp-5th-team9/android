package org.app.presentation.schedule.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moball.app.R
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun ScheduleCalendarDialog(
    initialDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    var displayMonth by remember(initialDate) { mutableStateOf(YearMonth.from(initialDate)) }
    var selectedDate by remember(initialDate) { mutableStateOf<LocalDate?>(initialDate) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        ScheduleCalendarDialogContent(
            displayMonth = displayMonth,
            selectedDate = selectedDate,
            onPrevMonth = { displayMonth = displayMonth.minusMonths(1) },
            onNextMonth = { displayMonth = displayMonth.plusMonths(1) },
            onDateSelect = { selectedDate = it },
            onConfirm = { selectedDate?.let(onConfirm) },
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun ScheduleCalendarDialogContent(
    displayMonth: YearMonth,
    selectedDate: LocalDate?,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val today = LocalDate.now()
    val days = remember(displayMonth) { getDaysInMonth(displayMonth) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .widthIn(max = 360.dp)
            .background(MoballTheme.colors.backgroundBase, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CalendarHeader(
            displayMonth = displayMonth,
            onPrevMonth = onPrevMonth,
            onNextMonth = onNextMonth,
        )

        Spacer(modifier = Modifier.height(22.dp))

        DayOfWeekHeader()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth(),
            userScrollEnabled = false,
        ) {
            items(days) { date ->
                if (date != null) {
                    DateCell(
                        date = date,
                        isToday = date == today,
                        isSelected = date == selectedDate,
                        onClick = { onDateSelect(date) },
                    )
                } else {
                    Spacer(modifier = Modifier.aspectRatio(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MoballButton(
                text = "취소",
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                backgroundColor = MoballTheme.colors.backgroundSurface,
                textColor = MoballTheme.colors.textSecondary,
            )
            MoballButton(
                text = "확인",
                onClick = onConfirm,
                modifier = Modifier.weight(4f),
                backgroundColor = MoballTheme.colors.accentPrimary,
                textColor = MoballTheme.colors.textPrimary,
                enabled = selectedDate != null,
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    displayMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_left),
            contentDescription = "이전 달",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable(onClick = onPrevMonth),
        )

        Text(
            text = "${displayMonth.year}년 ${displayMonth.monthValue}월",
            style = MoballTheme.typography.heading5.bold18,
            color = MoballTheme.colors.textPrimary,
        )

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_right),
            contentDescription = "다음 달",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable(onClick = onNextMonth),
        )
    }
}

@Composable
private fun DayOfWeekHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        listOf("일", "월", "화", "수", "목", "금", "토").forEachIndexed { index, day ->
            val color = when (index) {
                0 -> MoballTheme.colors.calendarSunday
                else -> MoballTheme.colors.textPrimary
            }
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                style = MoballTheme.typography.body.medium14,
                color = color,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DateCell(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isSelected -> Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MoballTheme.colors.textPrimary),
            )

            isToday -> Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(1.5.dp, MoballTheme.colors.accentPrimary, CircleShape),
            )
        }

        Text(
            text = date.dayOfMonth.toString(),
            style = MoballTheme.typography.body.medium16,
            color = when {
                isSelected -> MoballTheme.colors.staticWhite
                isSunday -> MoballTheme.colors.calendarSunday
                else -> MoballTheme.colors.textPrimary
            },
            textAlign = TextAlign.Center,
        )
    }
}

private fun getDaysInMonth(yearMonth: YearMonth): List<LocalDate?> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()

    val days = mutableListOf<LocalDate?>()
    repeat(firstDayOfWeek) { days.add(null) }
    for (day in 1..daysInMonth) {
        days.add(yearMonth.atDay(day))
    }
    return days
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ScheduleCalendarDialogPreview() {
    MoballTheme {
        var displayMonth by remember { mutableStateOf(YearMonth.now()) }
        var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

        ScheduleCalendarDialogContent(
            displayMonth = displayMonth,
            selectedDate = selectedDate,
            onPrevMonth = { displayMonth = displayMonth.minusMonths(1) },
            onNextMonth = { displayMonth = displayMonth.plusMonths(1) },
            onDateSelect = { selectedDate = it },
            onConfirm = {},
            onDismiss = {},
        )
    }
}
