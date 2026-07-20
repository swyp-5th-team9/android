package org.app.presentation.mypage.favorite.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.UrlImage
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.mypage.favorite.FavoritePubItem

@Composable
fun FavoriteItemCard(
    item: FavoritePubItem,
    isEditMode: Boolean,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onHeartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onCardClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MoballTheme.colors.backgroundSurface),
        ) {
            UrlImage(
                url = item.thumbnailImageUrl,
                contentDescription = item.pubName ?: "",
                contentScale = ContentScale.Crop,
                placeholderRes = R.drawable.img_favorite_item,
                modifier = Modifier.matchParentSize(),
            )

            if (isEditMode) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MoballTheme.colors.backgroundScrim.copy(alpha = if (isSelected) 0.35f else 0.12f)),
                )
            }

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_wish_heart_fill),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 6.dp, bottom = 6.dp)
                    .noRippleClickable {
                        if (!isEditMode) onHeartClick()
                    },
            )

            if (isEditMode) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (isSelected) R.drawable.ic_checkbox_fill else R.drawable.ic_checkbox,
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .size(24.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.pubName ?: "알 수 없는 펍",
            style = MoballTheme.typography.heading6.bold16,
            color = MoballTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        item.address?.let {
            Text(
                text = it,
                style = MoballTheme.typography.caption.medium12,
                color = MoballTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(name = "편집 모드 - 선택됨", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun FavoriteItemCardEditSelectedPreview() {
    MoballTheme {
        FavoriteItemCard(
            item = FavoritePubItem(favoriteId = 1L, pubId = 12L, pubName = "야구펍 홍대점", address = "홍대"),
            isEditMode = true,
            isSelected = true,
            onCardClick = {},
            onHeartClick = {},
            modifier = Modifier.size(124.dp, 172.dp),
        )
    }
}

@Preview(name = "편집 모드 - 미선택", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun FavoriteItemCardEditUnselectedPreview() {
    MoballTheme {
        FavoriteItemCard(
            item = FavoritePubItem(favoriteId = 2L, pubId = 15L, pubName = "롯데 응원 맛집", address = "잠실"),
            isEditMode = true,
            isSelected = false,
            onCardClick = {},
            onHeartClick = {},
            modifier = Modifier.size(124.dp, 172.dp),
        )
    }
}
