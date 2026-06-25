package org.app.presentation.mypage.wishlist.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
fun WishlistEditButton(
    hasSelection: Boolean,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MoballTheme.colors.backgroundBase)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(MoballTheme.colors.borderNormal, RoundedCornerShape(12.dp))
                .noRippleClickable(onClick = onCancel),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "취소",
                style = MoballTheme.typography.heading5.semibold18,
                color = MoballTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 80.dp, vertical = 15.dp),
            )
        }

        val deleteContainerColor =
            if (hasSelection) MoballTheme.colors.backgroundScrim else MoballTheme.colors.borderNormal
        val deleteContentColor =
            if (hasSelection) MoballTheme.colors.textPrimaryInverse else MoballTheme.colors.textQuaternary

        Box(
            modifier = Modifier
                .weight(1f)
                .background(deleteContainerColor, RoundedCornerShape(12.dp))
                .noRippleClickable { if (hasSelection) onDelete() }
                .padding(vertical = 16.dp),
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
private fun WishlistEditButtonPreview() {
    MoballTheme {
        WishlistEditButton(
            hasSelection = true,
            onCancel = {},
            onDelete = {},
        )
    }
}
