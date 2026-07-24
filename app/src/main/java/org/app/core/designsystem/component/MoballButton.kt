package org.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.designsystem.theme.MoballTheme.colors
import org.app.core.designsystem.theme.MoballTheme.typography
import org.app.core.extension.noRippleClickable

@Composable
fun MoballButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = colors.accentPrimary,
    textColor: Color = colors.textPrimary,
    disabledBackgroundColor: Color = colors.iconDisabled,
    disabledTextColor: Color = colors.textQuaternary,
) {
    val resolvedBackgroundColor = if (enabled) backgroundColor else disabledBackgroundColor
    val resolvedTextColor = if (enabled) textColor else disabledTextColor

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(resolvedBackgroundColor)
            .then(if (enabled) Modifier.noRippleClickable(onClick = onClick) else Modifier)
            .heightIn(min = 56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = typography.heading6.semibold16,
            color = resolvedTextColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MoballButtonPreview() {
    MoballTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MoballButton(
                text = "활성화 버튼",
                onClick = {},
                enabled = true,
            )
            MoballButton(
                text = "비활성화 버튼",
                onClick = {},
                enabled = false,
            )
        }
    }
}
