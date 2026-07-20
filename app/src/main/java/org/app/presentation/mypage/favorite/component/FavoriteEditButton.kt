package org.app.presentation.mypage.favorite.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun FavoriteEditButton(
    hasSelection: Boolean,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MoballTheme.colors.backgroundBase)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(MoballTheme.colors.backgroundBase, RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = MoballTheme.colors.borderNormal,
                    shape = RoundedCornerShape(12.dp),
                ).noRippleClickable(onClick = onCancel)
                .heightIn(min = 56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "취소",
                style = MoballTheme.typography.heading5.semibold18,
                color = MoballTheme.colors.textPrimary,
            )
        }

        val deleteContainerColor =
            if (hasSelection) MoballTheme.colors.accentPrimary else MoballTheme.colors.backgroundSurface
        val deleteContentColor =
            if (hasSelection) MoballTheme.colors.textPrimary else MoballTheme.colors.textQuaternary

        Box(
            modifier = Modifier
                .weight(3f)
                .background(deleteContainerColor, RoundedCornerShape(12.dp))
                .noRippleClickable { if (hasSelection) onDelete() }
                .heightIn(min = 56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "삭제",
                style = MoballTheme.typography.heading5.semibold18,
                color = deleteContentColor,
            )
        }
    }
}

@Preview
@Composable
private fun FavoriteEditButtonPreview() {
    MoballTheme {
        FavoriteEditButton(
            hasSelection = true,
            onCancel = {},
            onDelete = {},
        )
    }
}
