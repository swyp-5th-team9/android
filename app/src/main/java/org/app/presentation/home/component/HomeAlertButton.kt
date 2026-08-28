package org.app.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

/** 홈 상단 검색바 우측의 알림 버튼 (56dp 흰색 원형, 검색바와 동일한 그림자). */
@Composable
fun HomeAlertButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(
                elevation = 3.dp,
                shape = CircleShape,
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000),
            ).shadow(
                elevation = 4.dp,
                shape = CircleShape,
                spotColor = Color(0x14000000),
                ambientColor = Color(0x14000000),
            ).background(color = MoballTheme.colors.backgroundBase, shape = CircleShape)
            .noRippleClickable(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_bell),
            contentDescription = "알림",
            tint = MoballTheme.colors.iconPrimary,
            modifier = Modifier.size(24.dp),
        )
    }
}
