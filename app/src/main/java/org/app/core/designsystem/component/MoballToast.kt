package org.app.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme

private val ToastShape = RoundedCornerShape(12.dp)

@Composable
fun MoballToast(
    message: String,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = ToastShape)
            .background(color = MoballTheme.colors.accentTertiaryLight, shape = ToastShape)
            .border(width = 1.dp, color = MoballTheme.colors.accentPrimary, shape = ToastShape)
            .heightIn(min = 44.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIconRes != null) {
            Icon(
                imageVector = ImageVector.vectorResource(leadingIconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = message,
            style = MoballTheme.typography.heading7.semibold14,
            color = MoballTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MoballToastPreview() {
    MoballTheme {
        MoballToast(message = "즐겨찾기 삭제가 완료되었어요.")
    }
}
