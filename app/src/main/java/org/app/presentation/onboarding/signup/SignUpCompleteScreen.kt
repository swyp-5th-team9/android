package org.app.presentation.onboarding.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.theme.MoballTheme

/**
 * TODO nickname은 SignUpViewModel state에서 수집하거나 nav arg로 전달
 */
@Composable
fun SignUpCompleteRoute(
    nickname: String,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SignUpCompleteScreen(
        nickname = nickname,
        onStartClick = navigateToHome,
        modifier = modifier,
    )
}

@Composable
private fun SignUpCompleteScreen(
    nickname: String,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFF31353B),
                        0.56f to Color(0xFF31353B),
                        1.0f to Color(0xFF1C1C1C),
                    ),
                ),
            ).padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(83.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                    append("$nickname 님,")
                }
            },
            style = MoballTheme.typography.heading1.extrabold26,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MoballTheme.colors.accentPrimary, fontWeight = FontWeight.Bold)) {
                    append("모여볼")
                }
                withStyle(SpanStyle(color = MoballTheme.colors.textPrimaryInverse, fontWeight = FontWeight.Bold)) {
                    append("에 오신 걸 환영해요!")
                }
            },
            style = MoballTheme.typography.heading1.extrabold26,
        )

        Spacer(modifier = Modifier.height(9.dp))

        Text(
            text = "오늘부터 같이 응원할 펍을 찾아드릴게요",
            style = MoballTheme.typography.body.medium16,
            color = MoballTheme.colors.textDisabled,
        )

        Spacer(modifier = Modifier.height(118.dp))

        Image(
            painter = painterResource(R.drawable.img_moball_complete),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.weight(1f))

        MoballButton(
            text = "시작하기",
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp),
        )
    }
}

@Preview
@Composable
private fun SignUpCompleteScreenPreview() {
    MoballTheme {
        SignUpCompleteScreen(
            nickname = "홍길동",
            onStartClick = {},
        )
    }
}
