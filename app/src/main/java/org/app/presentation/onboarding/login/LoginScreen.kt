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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch
import org.app.core.common.util.CollectSideEffect
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.domain.model.SocialType
import org.app.presentation.onboarding.login.component.SocialLoginButton

private const val PRIVACY_POLICY_URL =
    "https://puzzle-visor-003.notion.site/390b8196eb4880b881f7f5641e7fc72f?source=copy_link"

@Composable
fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val loginManagers = remember(context) {
        EntryPointAccessors
            .fromApplication(context.applicationContext, SocialLoginManagerEntryPoint::class.java)
            .socialLoginManagers()
    }

    CollectSideEffect(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            LoginContract.SideEffect.NavigateToHome -> navigateToHome()

            LoginContract.SideEffect.NavigateToSignUp -> navigateToSignUp()

            is LoginContract.SideEffect.ShowToast ->
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun startSocialLogin(type: SocialType) {
        if (state.isLoading) return
        val manager = loginManagers[type]
        if (manager == null) {
            viewModel.onEvent(
                LoginContract.Event.OnSocialLoginFailed(
                    type = type,
                    throwable = IllegalStateException("지원하지 않는 로그인 방식입니다: $type"),
                ),
            )
            return
        }
        scope.launch {
            manager
                .login(context = context)
                .onSuccess { token ->
                    viewModel.onEvent(LoginContract.Event.OnSocialTokenReceived(type = type, token = token))
                }.onFailure { throwable ->
                    viewModel.onEvent(LoginContract.Event.OnSocialLoginFailed(type = type, throwable = throwable))
                }
        }
    }

    LoginScreen(
        onKakaoLoginClick = { startSocialLogin(SocialType.KAKAO) },
        onNaverLoginClick = { startSocialLogin(SocialType.NAVER) },
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
            )
            // 배경은 시스템바 뒤까지 채우고, 콘텐츠만 시스템바 안쪽으로 인셋
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
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
