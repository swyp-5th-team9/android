package org.app.presentation.mypage.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun ReportCategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (isSelected) MoballTheme.colors.accentPrimary else MoballTheme.colors.backgroundBase
    val textColor = if (isSelected) MoballTheme.colors.textPrimary else MoballTheme.colors.textSecondary
    val borderColor = if (isSelected) Color.Transparent else MoballTheme.colors.borderStrong

    Box(
        modifier = modifier
            .background(bgColor, CircleShape)
            .border(1.dp, borderColor, CircleShape)
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MoballTheme.typography.heading6.semibold16,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportCategoryChipPreview() {
    MoballTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ReportCategoryChip(
                label = "문의하기",
                isSelected = false,
                onClick = {},
            )

            ReportCategoryChip(
                label = "신고하기",
                isSelected = true,
                onClick = {},
            )
        }
    }
}
