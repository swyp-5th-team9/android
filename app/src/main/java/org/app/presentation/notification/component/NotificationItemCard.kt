package org.app.presentation.notification.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.notification.NotificationItem

/**
 * 알림 목록 아이템 카드.
 * 상단: [제목] [날짜] ...(우측) 더보기(삭제) / 하단: 본문 메시지.
 */
@Composable
fun NotificationItemCard(
    item: NotificationItem,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MoballTheme.colors.backgroundPage,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.title,
                    style = MoballTheme.typography.heading6.semibold16,
                    color = MoballTheme.colors.textTitle,
                )
                Spacer(modifier = Modifier.width(11.dp))
                Text(
                    text = item.date,
                    style = MoballTheme.typography.body.medium14,
                    color = MoballTheme.colors.textTertiary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_more_vertical),
                    contentDescription = "더보기",
                    tint = MoballTheme.colors.iconTertiary,
                    modifier = Modifier
                        .size(24.dp)
                        .noRippleClickable(onClick = onMoreClick),
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = item.message,
                style = MoballTheme.typography.body.medium14,
                color = MoballTheme.colors.textSecondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationItemCardPreview() {
    MoballTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NotificationItemCard(
                item = NotificationItem(
                    id = 1L,
                    title = "경기 일정 알림",
                    message = "LG 트윈스 경기가 오늘 오후 6시에 있어요!",
                    date = "8월 15일",
                ),
                onMoreClick = {},
            )
        }
    }
}
