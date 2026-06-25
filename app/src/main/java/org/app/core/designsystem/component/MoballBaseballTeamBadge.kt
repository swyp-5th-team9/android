package org.app.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme

@Composable
fun MoballBaseBallTeamBadge(
    teamName: String,
    modifier: Modifier = Modifier,
) {
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
            modifier = modifier,
        )
    } else {
        Text(
            text = teamName,
            style = MoballTheme.typography.heading5.extrabold18,
            color = MoballTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = modifier,
        )
    }
}
