package org.app.presentation.onboarding.signup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.component.textfield.MoballLineTextField
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.onboarding.signup.component.OnboardingProgressBar

@Composable
fun SignUpNicknameRoute(
    onBack: () -> Unit,
    navigateToTeamSelection: (nickname: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val textFieldState = rememberTextFieldState(initialText = state.nickname)

    LaunchedEffect(textFieldState.text) {
        viewModel.onEvent(SignUpContract.Event.OnNicknameChanged(textFieldState.text.toString()))
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SignUpContract.SideEffect.NavigateToTeamSelection -> {
                    navigateToTeamSelection(effect.nickname)
                }

                SignUpContract.SideEffect.NavigateBack -> {
                    onBack()
                }

                is SignUpContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
        }
    }

    SignUpNicknameScreen(
        state = state,
        textFieldState = textFieldState,
        onNextClick = { viewModel.onEvent(SignUpContract.Event.OnNicknameNext) },
        onBack = { viewModel.onEvent(SignUpContract.Event.OnBack) },
        modifier = modifier,
    )
}

@Composable
private fun SignUpNicknameScreen(
    state: SignUpContract.State,
    textFieldState: TextFieldState,
    onNextClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(
            state = TopBarState.Back(
                title = "",
                onBackClick = onBack,
            ),
            centerContent = { OnboardingProgressBar(totalSteps = 2, currentStep = 1) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "어떻게 불러드릴까요?",
                style = MoballTheme.typography.heading1.bold26,
                color = MoballTheme.colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(48.dp))

            MoballLineTextField(
                state = textFieldState,
                placeholder = "사용할 닉네임을 적어주세요",
                label = "닉네임",
                maxLength = 20,
                showCharCount = true,
                isError = state.nicknameError != null,
                errorMessage = state.nicknameError,
            )

            Spacer(modifier = Modifier.weight(1f))

            MoballButton(
                text = "이걸로 할래요",
                onClick = onNextClick,
                enabled = state.isNicknameValid,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SignUpNicknameScreenEmptyPreview() {
    MoballTheme {
        SignUpNicknameScreen(
            state = SignUpContract.State(),
            textFieldState = rememberTextFieldState(),
            onNextClick = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SignUpNicknameScreenFilledPreview() {
    MoballTheme {
        SignUpNicknameScreen(
            state = SignUpContract.State(nickname = "홍길동"),
            textFieldState = rememberTextFieldState("홍길동"),
            onNextClick = {},
            onBack = {},
        )
    }
}
