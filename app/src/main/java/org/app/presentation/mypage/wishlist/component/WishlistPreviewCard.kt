package org.app.presentation.mypage.wishlist.component

import android.R.attr.maxLines
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.UrlImage
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun WishlistPreviewCard(
    pubName: String,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    location: String? = null,
) {
    Column(
        modifier = modifier
            .width(162.dp)
            .clip(RoundedCornerShape(16.dp))
            .noRippleClickable(onClick = onClick),
    ) {
        UrlImage(
            url = imageUrl.takeIf { !it.isNullOrBlank() } ?: R.drawable.img_wish_item,
            modifier = Modifier
                .aspectRatio(160f / 99f)
                .background(MoballTheme.colors.backgroundSurface),
            contentScale = ContentScale.Crop,
            placeholderRes = R.drawable.img_wish_item,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoballTheme.colors.textPrimary)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
        ) {
            Text(
                text = pubName,
                style = MoballTheme.typography.heading7.bold14,
                color = MoballTheme.colors.textPrimaryInverse,
                maxLines = 1,
            )
            if (!location.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = location,
                    style = MoballTheme.typography.caption.medium12,
                    color = MoballTheme.colors.textPrimaryInverse,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WishlistPreviewCardPreview() {
    MoballTheme {
        WishlistPreviewCard(
            pubName = "버드나무 브루어리",
            imageUrl = null,
            onClick = {},
            location = "강릉",
            modifier = Modifier.padding(16.dp),
        )
    }
}
