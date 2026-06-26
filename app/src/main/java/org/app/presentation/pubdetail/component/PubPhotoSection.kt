package org.app.presentation.pubdetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
        Spacer(modifier = Modifier.height(10.dp))

        if (imageUrls.isEmpty()) {
            Text(
                text = "제공된 사진이 없습니다.",
                style = MoballTheme.typography.body.regular14,
                color = MoballTheme.colors.textTertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                maxLines = 3,
                maxItemsInEachRow = 3,
            ) {
                imageUrls.forEachIndexed { index, url ->
                    UrlImage(
                        url = url,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .noRippleClickable { onPhotoClick(index) },
                        contentScale = ContentScale.Crop,
                        contentDescription = "매장 사진 ${index + 1}",
                        placeholderRes = R.drawable.img_moball_empty,
                    )
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
