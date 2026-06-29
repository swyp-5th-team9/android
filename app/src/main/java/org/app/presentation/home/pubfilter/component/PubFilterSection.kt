package org.app.presentation.home.pubfilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.home.model.PubFilterOption
import org.app.presentation.home.model.PubFilterSection

/**
 * 필터 전체화면의 섹션 하나
 * @param section          섹션 모델 (title + options)
 * @param selectedOptionIds 현재 선택된 옵션 ID 집합
 * @param onToggle         (optionId) 클릭 콜백
 */
@Composable
fun PubFilterSection(
    section: PubFilterSection,
    selectedOptionIds: Set<String>,
    onToggle: (optionId: String) -> Unit,
    modifier: Modifier = Modifier,
    showTitle: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showTitle) {
            Text(
                text = section.title,
                style = MoballTheme.typography.heading6.bold16,
                color = MoballTheme.colors.textSecondary,
            )
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            section.options.forEach { option ->
                PubFilterOptionChip(
                    label = option.label,
                    isSelected = option.id in selectedOptionIds,
                    onToggle = { onToggle(option.id) },
                )
            }
        }
    }
}

@Composable
fun PubFilterOptionChip(
    label: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (isSelected) MoballTheme.colors.accentPrimary else MoballTheme.colors.backgroundBase
    val borderColor = if (isSelected) MoballTheme.colors.accentPrimary else MoballTheme.colors.borderNormal
    val textColor = if (isSelected) MoballTheme.colors.textPrimary else MoballTheme.colors.textSecondary

    Text(
        text = label,
        style = MoballTheme.typography.heading7.semibold14,
        color = textColor,
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .noRippleClickable(onToggle)
            .padding(horizontal = 23.dp, vertical = 12.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun PubFilterSectionPreview() {
    MoballTheme {
        PubFilterSection(
            section = PubFilterSection(
                sectionId = "team",
                title = "응원팀",
                options = listOf(
                    PubFilterOption("kia", "KIA"),
                    PubFilterOption("samsung", "삼성"),
                    PubFilterOption("lg", "LG"),
                    PubFilterOption("doosan", "두산"),
                    PubFilterOption("kt", "KT"),
                ),
            ),
            selectedOptionIds = setOf("kia", "lg"),
            onToggle = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
