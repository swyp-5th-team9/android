package org.app.presentation.mypage.component

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.MoballBaseBallTeamBadge
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun MyPageAddSportsCard(
    supportedTeams: List<String>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFAFAFB),
                shape = RoundedCornerShape(16.dp),
            ).padding(vertical = 11.dp, horizontal = 20.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_edit_nickname_pencil),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.End)
                .noRippleClickable(onAddClick),
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (supportedTeams.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 19.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                supportedTeams.take(3).forEach { team ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        MoballBaseBallTeamBadge(teamName = team)
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
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
