package org.app.presentation.onboarding.login

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.domain.model.SocialType
import org.app.presentation.onboarding.login.component.SocialLoginButton

// TODO 개인정보처리방침 URL — 추후 실제 URL로 교체
private const val PRIVACY_POLICY_URL = "https://moball.kr/privacy"

@Composable
fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                LoginContract.SideEffect.NavigateToHome -> {
                    navigateToHome()
                }

                LoginContract.SideEffect.NavigateToSignUp -> {
                    navigateToSignUp()
                }

                is LoginContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LoginScreen(
        onKakaoLoginClick = { viewModel.login(type = SocialType.KAKAO, context = context) },
        onNaverLoginClick = { viewModel.login(type = SocialType.NAVER, context = context) },
        onPrivacyPolicyClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)))
        },
        modifier = modifier,
    )
}

@Composable
private fun LoginScreen(
    onKakaoLoginClick: () -> Unit,
    onNaverLoginClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.ic_moball_logo),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Image(
            painter = painterResource(R.drawable.img_moball_text),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )

        Spacer(modifier = Modifier.height(29.dp))

        Image(
            painter = painterResource(R.drawable.img_splash_slogan1),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.weight(1f))

        SocialLoginButton(
            iconRes = R.drawable.ic_naver_logo,
            label = "네이버로 시작하기",
            onClick = onNaverLoginClick,
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialLoginButton(
            iconRes = R.drawable.ic_kakao_logo,
            label = "카카오로 시작하기",
            onClick = onKakaoLoginClick,
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = MoballTheme.colors.textTertiary,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 12.sp,
                    ),
                ) {
                    append("개인정보처리방침")
                }
            },
            modifier = Modifier
                .padding(bottom = 10.dp)
                .noRippleClickable(onClick = onPrivacyPolicyClick),
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MoballTheme {
        LoginScreen(
            onKakaoLoginClick = {},
            onNaverLoginClick = {},
            onPrivacyPolicyClick = {},
        )
    }
}
