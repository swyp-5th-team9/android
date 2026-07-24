package org.app.presentation.pubdetail.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun PubBottomBar(
    hasPhoneNumber: Boolean,
    onPhoneCall: () -> Unit,
    onKakaoMapClick: () -> Unit,
    onNaverMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MoballTheme.colors.staticWhite,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            // 내비바 인셋을 버튼 패딩에서 분리해 바 하단에만 둔다.
            // (버튼 Row는 위아래 25dp 대칭 유지 → 위/아래 간격 동일)
            modifier = Modifier.navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 25.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (hasPhoneNumber) {
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = MoballTheme.colors.borderStrong,
                                shape = RoundedCornerShape(12.dp),
                            ).noRippleClickable(onPhoneCall),
                        color = MoballTheme.colors.backgroundBase,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }

                MapButton(
                    text = "카카오맵",
                    onClick = onKakaoMapClick,
                    modifier = Modifier.weight(1f),
                )
                MapButton(
                    text = "네이버지도",
                    onClick = onNaverMapClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MapButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .heightIn(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .noRippleClickable(onClick),
        shape = RoundedCornerShape(12.dp),
        color = MoballTheme.colors.backgroundScrim,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MoballTheme.typography.heading5.semibold18,
                color = MoballTheme.colors.staticWhite,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_up_right_md),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PubBottomBarPreview() {
    MoballTheme {
        PubBottomBar(
            hasPhoneNumber = true,
            onPhoneCall = {},
            onKakaoMapClick = {},
            onNaverMapClick = {},
        )
    }
}
