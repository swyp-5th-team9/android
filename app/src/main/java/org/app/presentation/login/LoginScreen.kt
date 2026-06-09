package org.app.presentation.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moball.app.R.drawable.img_kakao_login
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun LoginRoute(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                LoginContract.SideEffect.NavigateToHome -> navigateToHome()
                is LoginContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LoginScreen(
        onKakaoLoginClick = {
            viewModel.login(type = LoginContract.SocialType.KAKAO, context = context)
        },
        onNaverLoginClick = {
            viewModel.login(type = LoginContract.SocialType.NAVER, context = context)
        },
        modifier = modifier,
    )
}

@Composable
private fun LoginScreen(
    onKakaoLoginClick: () -> Unit,
    onNaverLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: 추후 수정 예정
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = img_kakao_login),
            contentDescription = null,
            modifier = Modifier.noRippleClickable(
                onClick = onKakaoLoginClick,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 네이버 버튼 임시 (디자인 나오면 수정)
        Box(
            modifier = Modifier
                .noRippleClickable(onClick = onNaverLoginClick)
                .background(
                    androidx.compose.ui.graphics
                        .Color(0xFF03C75A),
                ) // 네이버 공식 그린 색상
                .padding(16.dp),
        ) {
            Text(text = "네이버 로그인", color = androidx.compose.ui.graphics.Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MoballTheme {
        LoginScreen(
            onKakaoLoginClick = {},
            onNaverLoginClick = {},
        )
    }
}
