package org.app.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R.drawable
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

/**
 * 검색 리스트 아이템
 * @param text      표시할 검색어 또는 장소명
 * @param onClick   아이템 클릭 콜백
 * @param modifier  외부 Modifier
 * @param leadingIcon 좌측 아이콘 (기본: 검색 아이콘)
 */
@Composable
fun SearchListItemTextField(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = ImageVector.vectorResource(drawable.ic_search),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MoballTheme.colors.backgroundBase)
            .noRippleClickable(onClick)
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // 좌측 아이콘 영역 (피그마 아이콘 버튼 48×48)
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = MoballTheme.colors.iconTertiary,
            modifier = Modifier
                .size(48.dp)
                .padding(14.dp), // 아이콘 실제 크기 20dp = 48dp - 14dp*2
        )

        Text(
            text = text,
            style = MoballTheme.typography.body.medium14,
            color = MoballTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchListItemPreview() {
    MoballTheme {
        SearchListItemTextField(
            text = "펍이름",
            onClick = {},
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchListItemLongTextPreview() {
    MoballTheme {
        SearchListItemTextField(
            text = "강남구 역삼동 맥주집",
            onClick = {},
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}
