package org.app.presentation.mypage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

/**
 * 마이페이지 설정 카드
 * @param items 설정 항목 목록 (순서대로 구분선으로 분리되어 표시)
 */
@Composable
fun MyPageSettingCard(
    items: List<MyPageSettingItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MoballTheme.colors.borderStrong,
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        items.forEachIndexed { index, item ->
            MyPageSettingRow(item = item)
            if (index < items.lastIndex) {
                HorizontalDivider(color = MoballTheme.colors.borderStrong, thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun MyPageSettingRow(
    item: MyPageSettingItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = item.onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(item.iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(24.dp),
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        ) {
            Text(
                text = item.title,
                style = MoballTheme.typography.heading6.semibold16,
                color = MoballTheme.colors.textPrimary,
            )

            item.subtitle?.let { sub ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sub,
                    style = MoballTheme.typography.caption.medium12,
                    color = MoballTheme.colors.textTertiary,
                )
            }
        }

        val toggle = item.toggle
        if (toggle != null) {
            NotificationSwitch(
                checked = toggle.checked,
                onCheckedChange = toggle.onCheckedChange,
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

data class MyPageSettingItem(
    @get:DrawableRes val iconRes: Int,
    val title: String,
    val subtitle: String? = null,
    val onClick: () -> Unit = {},
    // 지정 시 우측에 스위치를 표시(이동용 chevron 대신). 알림 설정 등 토글 행에 사용.
    val toggle: MyPageSettingToggle? = null,
)

data class MyPageSettingToggle(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
)

@Preview(showBackground = true)
@Composable
fun MyPageSettingCardPreview() {
    MoballTheme {
        var notificationEnabled by remember { mutableStateOf(true) }
        Box(
            modifier = Modifier.padding(16.dp),
        ) {
            MyPageSettingCard(
                items = listOf(
                    MyPageSettingItem(
                        iconRes = R.drawable.ic_sound,
                        title = "제보하기",
                        subtitle = "잘못된 정보, 앱 오류 신고",
                        onClick = {},
                    ),
                    MyPageSettingItem(
                        iconRes = R.drawable.ic_bell,
                        title = "알림 설정",
                        subtitle = "권한 설정",
                        toggle = MyPageSettingToggle(
                            checked = notificationEnabled,
                            onCheckedChange = { notificationEnabled = it },
                        ),
                    ),
                    MyPageSettingItem(
                        iconRes = R.drawable.ic_warning_info,
                        title = "약관 및 정책",
                        subtitle = "개인정보처리방침",
                        onClick = {},
                    ),
                    MyPageSettingItem(
                        iconRes = R.drawable.ic_logout,
                        title = "로그아웃",
                        onClick = {},
                    ),
                ),
            )
        }
    }
}
