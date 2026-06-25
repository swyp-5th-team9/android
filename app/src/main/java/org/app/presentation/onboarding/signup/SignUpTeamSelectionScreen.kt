package org.app.presentation.onboarding.signup

import android.widget.Toast
import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.onboarding.signup.component.OnboardingProgressBar
import org.app.presentation.onboarding.signup.component.OnboardingTeamItem

data class KboTeam(
    val id: Int,
    val shortName: String,
    val fullName: String,
    val city: String,
    @DrawableRes val logoRes: Int,
)

// TODO teamId는 API 구현 후 서버 명세에 맞게 수정
val KBO_TEAMS = listOf(
    KboTeam(1, "KIA", "기아 타이거즈", "광주", R.drawable.img_kia),
    KboTeam(2, "KT", "KT WIZ", "수원", R.drawable.img_kt),
    KboTeam(3, "LG", "LG 트윈스", "서울", R.drawable.img_lg),
    KboTeam(4, "NC", "NC 다이노스", "창원", R.drawable.img_nc),
    KboTeam(5, "SSG", "SSG 랜더스", "인천", R.drawable.img_ssg),
    KboTeam(6, "두산", "두산 베어스", "서울", R.drawable.img_doosan),
    KboTeam(7, "롯데", "롯데 자이언츠", "부산", R.drawable.img_lotte),
    KboTeam(8, "삼성", "삼성 라이온즈", "대구", R.drawable.img_samsung),
    KboTeam(9, "키움", "키움 히어로즈", "서울", R.drawable.img_kiwoom),
    KboTeam(10, "한화", "한화 이글스", "대전", R.drawable.img_hanwha),
)

@Composable
fun SignUpTeamSelectionRoute(
    nickname: String,
    onBack: () -> Unit,
    navigateToComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // TODO nav arg로 전달된 nickname을 ViewModel에 반영 (별도 ViewModel 인스턴스인 경우 대비)
    LaunchedEffect(nickname) {
        viewModel.onEvent(SignUpContract.Event.OnNicknameChanged(nickname))
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
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
                    Unit
                }
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
                items(KBO_TEAMS, key = { it.id }) { team ->
                    OnboardingTeamItem(
                        teamName = team.shortName,
                        fullName = team.fullName,
                        city = team.city,
                        logoRes = team.logoRes,
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
