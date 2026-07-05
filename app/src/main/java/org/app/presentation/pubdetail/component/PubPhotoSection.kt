package org.app.presentation.pubdetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.UrlImage
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PubPhotoSection(
    imageUrls: List<String>,
    onPhotoClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "매장 사진",
            style = MoballTheme.typography.heading6.bold16,
            color = MoballTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (imageUrls.isEmpty()) {
            Text(
                text = "제공된 사진이 없습니다.",
                style = MoballTheme.typography.body.regular14,
                color = MoballTheme.colors.textTertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp),
                textAlign = TextAlign.Center,
            )
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val itemSize = (maxWidth - 8.dp) / 3

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    maxLines = 3,
                    maxItemsInEachRow = 3,
                ) {
                    imageUrls.forEachIndexed { index, url ->
                        Box(
                            modifier = Modifier
                                .size(itemSize)
                                .clip(RoundedCornerShape(12.dp))
                                .noRippleClickable { onPhotoClick(index) },
                        ) {
                            UrlImage(
                                url = url,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                contentDescription = "매장 사진 ${index + 1}",
                                placeholderRes = R.drawable.img_moball_empty,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PubPhotoSectionEmptyPreview() {
    MoballTheme {
        PubPhotoSection(
            imageUrls = emptyList(),
            onPhotoClick = {},
        )
    }
}
