package org.app.presentation.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

// TODO 삭제 기능은 없는 확인 중, 그리고 안에 들어있는 구단 이미지 바뀔가능성 높음
@Composable
fun MyPageAddSportsCard(
    supportedTeams: List<String>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Sport",
            style = MoballTheme.typography.heading3.semibold20,
            color = MoballTheme.colors.textPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFAFAFB),
                    shape = RoundedCornerShape(16.dp),
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 11.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "응원구단",
                    style = MoballTheme.typography.heading5.bold18,
                    color = MoballTheme.colors.textPrimary,
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_edit_nickname_pencil),
                    contentDescription = "구단 추가",
                    tint = MoballTheme.colors.iconPrimary,
                    modifier = Modifier.noRippleClickable(onAddClick),
                )
            }

            if (supportedTeams.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 19.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterHorizontally),
                ) {
                    supportedTeams.take(3).forEach { team ->
                        TeamBadge(teamName = team)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// TODO 이미지 수정 가능성 높음
@Composable
private fun TeamBadge(teamName: String) {
    val teamIconRes = when (teamName) {
        "LG" -> R.drawable.img_lg
        "KT" -> R.drawable.img_kt
        "삼성" -> R.drawable.img_samsung
        "한화" -> R.drawable.img_hanwha
        "KIA" -> R.drawable.img_kia
        "두산" -> R.drawable.img_doosan
        "NC" -> R.drawable.img_nc
        "SSG" -> R.drawable.img_ssg
        "롯데" -> R.drawable.img_lotte
        "키움" -> R.drawable.img_kiwoom
        else -> null
    }

    if (teamIconRes != null) {
        Image(
            painter = painterResource(id = teamIconRes),
            contentDescription = teamName,
            contentScale = ContentScale.Fit,
        )
    } else {
        Text(
            text = teamName,
            style = MoballTheme.typography.heading5.extrabold18,
            color = MoballTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageAddSportsCardPreview() {
    MoballTheme {
        MyPageAddSportsCard(
            supportedTeams = listOf("한화", "KT", "삼성"),
            onAddClick = {},
        )
    }
}
