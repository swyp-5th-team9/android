package org.app.presentation.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import org.app.presentation.mypage.favorite.FavoritePubItem
import org.app.presentation.mypage.favorite.component.FavoritePreviewCard

@Composable
fun MyPageFavoriteSection(
    favoriteItems: List<FavoritePubItem>,
    onFavoriteClick: () -> Unit,
    onPubClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "펍 즐겨찾기 목록",
                style = MoballTheme.typography.heading3.semibold20,
                color = MoballTheme.colors.textPrimary,
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_standard),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .noRippleClickable { onFavoriteClick() },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (favoriteItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(
                        color = Color(0xFFFAFAFB),
                        shape = RoundedCornerShape(16.dp),
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(74.dp))
                Text(
                    text = "아직 즐겨찾기에 추가한 펍이 없어요",
                    style = MoballTheme.typography.body.regular14,
                    color = MoballTheme.colors.textTertiary,
                )
                Spacer(modifier = Modifier.height(74.dp))
            }
        } else {
            val previewItems = remember(favoriteItems) { favoriteItems.take(5) }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
            ) {
                items(previewItems, key = { it.favoriteId }) { item ->
                    FavoritePreviewCard(
                        pubName = item.pubName,
                        imageUrl = item.thumbnailImageUrl,
                        onClick = { onPubClick(item.pubId.toString()) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageFavoriteSectionEmptyPreview() {
    MoballTheme {
        MyPageFavoriteSection(
            favoriteItems = emptyList(),
            onFavoriteClick = {},
            onPubClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageFavoriteSectionPreview() {
    MoballTheme {
        MyPageFavoriteSection(
            favoriteItems = listOf(
                FavoritePubItem(favoriteId = 1L, pubId = 11L, pubName = "버드나무 브루어리", address = "강릉"),
                FavoritePubItem(favoriteId = 2L, pubId = 12L, pubName = "데블스도어", address = "고속터미널"),
                FavoritePubItem(favoriteId = 3L, pubId = 13L, pubName = "플레이볼", address = "홍대"),
            ),
            onFavoriteClick = {},
            onPubClick = {},
        )
    }
}
