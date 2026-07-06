package org.app.presentation.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.UrlImage
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun MyPageProfileCard(
    nickname: String,
    profileImageUrl: String?,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UrlImage(
                url = profileImageUrl.takeIf { !it.isNullOrBlank() } ?: R.drawable.img_profile,
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .background(MoballTheme.colors.backgroundSurface, shape = CircleShape)
                    .size(80.dp),
                contentScale = ContentScale.Crop,
                placeholderRes = R.drawable.img_profile,
                // 서버가 동일 URL에 이미지를 덮어쓰므로 캐시를 우회해 최신 이미지를 로드
                bypassCache = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nickname.ifEmpty { "닉네임" },
                style = MoballTheme.typography.heading5.bold18,
                color = MoballTheme.colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MoballTheme.colors.borderNormal,
                        shape = RoundedCornerShape(24.dp),
                    ).noRippleClickable { onEditProfileClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "내정보 수정",
                    style = MoballTheme.typography.body.regular14,
                    color = MoballTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageProfileCardPreview() {
    MoballTheme {
        MyPageProfileCard(
            nickname = "닉네임",
            profileImageUrl = null,
            onEditProfileClick = {},
        )
    }
}
