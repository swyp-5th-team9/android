package org.app.presentation.home.pubfilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun PubFilterSubRegionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    MoballTheme.colors.accentTertiaryLight
                } else {
                    MoballTheme.colors.backgroundBase
                },
            ).border(
                1.dp,
                if (isSelected) MoballTheme.colors.accentPrimary else MoballTheme.colors.borderNormal,
                RoundedCornerShape(12.dp),
            ).noRippleClickable(onClick)
            .heightIn(min = 44.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MoballTheme.typography.heading7.semibold14,
            color = MoballTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 19.dp),
        )
    }
}
