package org.app.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R.drawable
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.minTouchTarget
import org.app.core.extension.noRippleClickable

@Composable
fun HomeSearchTextField(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "펍 이름, 구단을 입력해 주세요.",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .minTouchTarget()
            .shadow(elevation = 3.dp, spotColor = Color(0x1A000000), ambientColor = Color(0x1A000000))
            .shadow(elevation = 4.dp, spotColor = Color(0x14000000), ambientColor = Color(0x14000000))
            .background(
                color = MoballTheme.colors.backgroundBase,
                shape = CircleShape,
            ).noRippleClickable(onSearchClick)
            .padding(start = 20.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = placeholder,
            style = MoballTheme.typography.body.medium16,
            color = MoballTheme.colors.textTertiary,
            modifier = Modifier.weight(1f),
        )

        Icon(
            imageVector = ImageVector.vectorResource(drawable.ic_home_search),
            contentDescription = null,
            tint = MoballTheme.colors.iconPrimary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchTextFieldPreview() {
    MoballTheme {
        HomeSearchTextField(
            onSearchClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
