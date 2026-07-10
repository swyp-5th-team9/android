package org.app.presentation.onboarding.signup.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun OnboardingTeamItem(
    teamName: String,
    city: String,
    @DrawableRes logoRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) {
                        MoballTheme.colors.textPrimaryInverse
                    } else {
                        MoballTheme.colors.backgroundSurface
                    },
                ).then(
                    if (isSelected) {
                        Modifier.border(2.dp, MoballTheme.colors.borderActive, RoundedCornerShape(16.dp))
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(logoRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .aspectRatio(1f),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = teamName,
            style = MoballTheme.typography.heading7.bold14,
            color = MoballTheme.colors.textPrimary,
        )

        Text(
            text = city,
            style = MoballTheme.typography.caption.medium12,
            color = MoballTheme.colors.textSecondary,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OnboardingTeamItemUnselectedPreview() {
    MoballTheme {
        OnboardingTeamItem(
            teamName = "기아 타이거즈",
            city = "광주",
            logoRes = R.drawable.img_kia,
            isSelected = false,
            onClick = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OnboardingTeamItemSelectedPreview() {
    MoballTheme {
        OnboardingTeamItem(
            teamName = "두산",
            city = "서울",
            logoRes = R.drawable.img_doosan,
            isSelected = true,
            onClick = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}
