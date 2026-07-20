package org.app.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.pubdetail.model.KboTeamType

@Composable
fun MoballBaseballTeamBadge(
    teamName: String,
    modifier: Modifier = Modifier,
) {
    // 매칭 실패(ALL 포함) 시 logoRes가 null → 팀명 텍스트로 대체
    val teamIconRes = KboTeamType.fromShortName(teamName).logoRes

    if (teamIconRes != null) {
        Image(
            painter = painterResource(id = teamIconRes),
            contentDescription = teamName,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(80.dp),
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
