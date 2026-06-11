package org.app.presentation.onboarding.signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.app.core.designsystem.theme.MoballTheme

@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    SignUpScreen(
        modifier = modifier,
    )
}

@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "SignUp Screen")
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    MoballTheme {
        SignUpScreen()
    }
}
