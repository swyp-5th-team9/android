package org.app.presentation.home.pubfilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.home.pubfilter.CITIES
import org.app.presentation.home.pubfilter.SEOUL_SUB_REGIONS

@Composable
fun PubFilterRegionSectionContent(
    selectedSubRegionIds: Set<String>,
    onToggleSubRegion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "지역",
            style = MoballTheme.typography.heading3.bold20,
            color = MoballTheme.colors.textPrimary,
        )

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CITIES.forEach { city ->
                CityChip(
                    label = city.label,
                    isSelected = city.id == "seoul",
                    isEnabled = city.isEnabled,
                    onClick = { /* MVP: 서울만 지원 */ },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MoballTheme.colors.backgroundSurface)
                .padding(16.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SEOUL_SUB_REGIONS.forEach { option ->
                    PubFilterSubRegionChip(
                        label = option.label,
                        isSelected = option.id in selectedSubRegionIds,
                        onClick = { onToggleSubRegion(option.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun CityChip(
    label: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (isEnabled && isSelected) {
        MoballTheme.colors.accentPrimary
    } else {
        MoballTheme.colors.accentDisabled
    }
    val textColor = when {
        !isEnabled -> MoballTheme.colors.textTertiary
        isSelected -> MoballTheme.colors.textPrimary
        else -> MoballTheme.colors.textPrimary
    }
    val borderColor = if (isEnabled && isSelected) {
        MoballTheme.colors.accentPrimary
    } else {
        MoballTheme.colors.borderNormal
    }

    Text(
        text = label,
        style = MoballTheme.typography.body.medium14,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .then(if (isEnabled) Modifier.noRippleClickable(onClick) else Modifier)
            .padding(horizontal = 28.dp, vertical = 12.dp),
    )
}
