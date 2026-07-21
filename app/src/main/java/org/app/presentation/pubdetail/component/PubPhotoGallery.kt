package org.app.presentation.pubdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.UrlImage
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.minTouchTarget
import org.app.core.extension.noRippleClickable

@Composable
fun PubPhotoGallery(
    imageUrls: List<String>,
    initialPage: Int,
    onClose: () -> Unit,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (imageUrls.size - 1).coerceAtLeast(0)),
        pageCount = { imageUrls.size.coerceAtLeast(1) },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChanged(page)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val url = imageUrls.getOrNull(page)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                UrlImage(
                    url = url,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                    contentDescription = "사진 ${page + 1}",
                    placeholderRes = R.drawable.img_moball_empty,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = Color.White,
                modifier = Modifier
                    .minTouchTarget()
                    .size(28.dp)
                    .noRippleClickable(onClose),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${pagerState.currentPage + 1} / ${imageUrls.size}",
                style = MoballTheme.typography.heading5.semibold18,
                color = Color.White,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PubPhotoGalleryPreview() {
    MoballTheme {
        PubPhotoGallery(
            imageUrls = listOf("url1", "url2", "url3"),
            initialPage = 0,
            onClose = {},
            onPageChanged = {},
        )
    }
}
