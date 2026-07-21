package org.app.presentation.mypage.withdraw.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.minTouchTarget
import org.app.core.extension.noRippleClickable

@Composable
fun WithdrawReasonItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .minTouchTarget()
            .background(MoballTheme.colors.backgroundBase)
            .noRippleClickable(onClick)
            .heightIn(min = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MoballTheme.colors.accentPrimary,
                unselectedColor = MoballTheme.colors.borderStrong,
            ),
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            style = MoballTheme.typography.heading6.semibold16,
            color = MoballTheme.colors.textPrimary,
        )
    }
}
