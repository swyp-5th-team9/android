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
import androidx.compose.material3.Text
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

        Spacer(modifier = Modifier.height(24.dp))

        if (supportedTeams.isEmpty()) {
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
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "아직 응원하는 구단이 없어요",
                    style = MoballTheme.typography.body.regular14,
                    color = MoballTheme.colors.textTertiary,
                )
                Spacer(modifier = Modifier.height(77.dp))
            }
        } else {
            val teams = supportedTeams.take(3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 19.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                val emptyWeight = (3 - teams.size).toFloat()
                if (emptyWeight > 0) {
                    Spacer(modifier = Modifier.weight(emptyWeight / 2))
                }

                teams.forEach { team ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        MoballBaseBallTeamBadge(teamName = team)
                    }
                }

                if (emptyWeight > 0) {
                    Spacer(modifier = Modifier.weight(emptyWeight / 2))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageAddSportsCardPreview() {
    MoballTheme {
        MyPageAddSportsCard(
            supportedTeams = listOf("KT", "삼성"),
            onAddClick = {},
        )
    }
}
