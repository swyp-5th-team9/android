package org.app.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

/** 지도 위 오른쪽 하단 "제보하기" 버튼 */
@Composable
fun HomeReportButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(100.dp))
            .clip(RoundedCornerShape(100.dp))
            .background(MoballTheme.colors.backgroundScrim)
            .noRippleClickable(onClick)
            .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "제보하기",
            style = MoballTheme.typography.body.medium14,
            color = MoballTheme.colors.textPrimaryInverse,
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_mic),
            contentDescription = null,
            tint = MoballTheme.colors.iconPrimaryInverse,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** 지도 오른쪽 현재 위치 버튼*/
@Composable
fun HomeMyLocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_my_location),
        contentDescription = "현재 위치",
        tint = MoballTheme.colors.iconPrimary,
        modifier = modifier
            .size(48.dp)
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(MoballTheme.colors.backgroundBase)
            .noRippleClickable(onClick)
            .padding(12.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFBFC6CF)
@Composable
private fun HomeReportButtonPreview() {
    MoballTheme {
        HomeReportButton(onClick = {}, modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFBFC6CF)
@Composable
private fun HomeMyLocationButtonPreview() {
    MoballTheme {
        HomeMyLocationButton(onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
