package org.app.presentation.onboarding.signup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.core.common.util.CollectSideEffect
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.domain.model.KboTeamType
import org.app.presentation.onboarding.signup.component.OnboardingProgressBar
import org.app.presentation.onboarding.signup.component.OnboardingTeamItem

@Composable
fun SignUpTeamSelectionRoute(
    onBack: () -> Unit,
    navigateToComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CollectSideEffect(viewModel.sideEffect) { effect ->
        when (effect) {
            SignUpContract.SideEffect.NavigateToComplete -> {
                navigateToComplete()
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

    SignUpTeamSelectionScreen(
        state = state,
        onTeamToggled = { viewModel.onEvent(SignUpContract.Event.OnTeamToggled(it)) },
        onConfirmClick = { viewModel.onEvent(SignUpContract.Event.OnTeamSelectionConfirm) },
        onSkipClick = { viewModel.onEvent(SignUpContract.Event.OnTeamSelectionSkip) },
        onBack = { viewModel.onEvent(SignUpContract.Event.OnBack) },
        modifier = modifier,
    )
}

@Composable
private fun SignUpTeamSelectionScreen(
    state: SignUpContract.State,
    onTeamToggled: (Int) -> Unit,
    onConfirmClick: () -> Unit,
    onSkipClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(
            state = TopBarState.BackWithSkip(
                title = "",
                onBackClick = onBack,
                onSkipClick = onSkipClick,
            ),
            centerContent = { OnboardingProgressBar(totalSteps = 2, currentStep = 2) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "가장 응원하는 팀을\n골라주세요",
                style = MoballTheme.typography.heading1.bold26,
                color = MoballTheme.colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "최대 3팀까지 등록 가능합니다.",
                style = MoballTheme.typography.body.regular14,
                color = MoballTheme.colors.textTertiary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                items(KboTeamType.teams, key = { it.id }) { team ->
                    // teams는 ALL(로고 없음)을 제외하므로 logoRes는 항상 존재
                    val logoRes = team.logoRes ?: return@items
                    OnboardingTeamItem(
                        teamName = team.fullName,
                        city = team.city,
                        logoRes = logoRes,
                        isSelected = team.id in state.selectedTeamIds,
                        onClick = { onTeamToggled(team.id) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MoballButton(
                text = "시작하기",
                onClick = onConfirmClick,
                enabled = state.selectedTeamIds.isNotEmpty() && !state.isLoading,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TeamSelectionScreenEmptyPreview() {
    MoballTheme {
        SignUpTeamSelectionScreen(
            state = SignUpContract.State(nickname = "홍길동"),
            onTeamToggled = {},
            onConfirmClick = {},
            onSkipClick = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TeamSelectionScreenSelectedPreview() {
    MoballTheme {
        SignUpTeamSelectionScreen(
            state = SignUpContract.State(
                nickname = "홍길동",
                selectedTeamIds = setOf(1, 6),
            ),
            onTeamToggled = {},
            onConfirmClick = {},
            onSkipClick = {},
            onBack = {},
        )
    }
}
