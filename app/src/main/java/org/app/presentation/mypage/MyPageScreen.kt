package org.app.presentation.mypage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.mypage.component.MyPageAddSportsCard
import org.app.presentation.mypage.component.MyPageProfileCard
import org.app.presentation.mypage.component.MyPageSettingCard
import org.app.presentation.mypage.component.MyPageSettingItem
import org.app.presentation.mypage.component.MyPageTeamSelectBottomSheet

@Composable
fun MyPageRoute(
    navigateToLogin: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToReport: () -> Unit,
    navigateToWithdraw: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                MyPageContract.SideEffect.NavigateToLogin -> {
                    navigateToLogin()
                }

                MyPageContract.SideEffect.NavigateToEditProfile -> {
                    navigateToEditProfile()
                }

                MyPageContract.SideEffect.NavigateToReport -> {
                    navigateToReport()
                }

                MyPageContract.SideEffect.NavigateToWithdraw -> {
                    navigateToWithdraw()
                }

                is MyPageContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    MyPageScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
private fun MyPageScreen(
    state: MyPageContract.State,
    onEvent: (MyPageContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTeamSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(state = TopBarState.Default(title = "마이페이지"))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            MyPageProfileCard(
                nickname = state.nickname,
                profileImageUrl = state.profileImageUrl,
                onEditProfileClick = { onEvent(MyPageContract.Event.OnEditProfileClick) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            MyPageAddSportsCard(
                supportedTeams = state.supportedTeams,
                onAddClick = { showTeamSheet = true },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "설정",
                style = MoballTheme.typography.heading3.semibold20,
                color = MoballTheme.colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            MyPageSettingCard(
                items = listOf(
                    MyPageSettingItem(
                        iconRes = R.drawable.ic_lock,
                        title = "개인정보 및 보안",
                        subtitle = "비밀번호 변경, 계정 보안",
                        onClick = { onEvent(MyPageContract.Event.OnSettingPrivacyClick) },
                    ),
                    MyPageSettingItem(
                        iconRes = R.drawable.ic_headphones,
                        title = "제보하기",
                        subtitle = "잘못된 정보, 앱 오류 신고",
                        onClick = { onEvent(MyPageContract.Event.OnReportClick) },
                    ),
                ),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "회원탈퇴",
                style = MoballTheme.typography.body.regular14,
                color = MoballTheme.colors.textTertiary,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(MyPageContract.Event.OnWithdrawClick) },
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showTeamSheet) {
            MyPageTeamSelectBottomSheet(
                selectedTeams = state.supportedTeams,
                onTeamClick = { team -> onEvent(MyPageContract.Event.OnTeamSelected(team)) },
                onApply = { showTeamSheet = false },
                onDismiss = {
                    onEvent(MyPageContract.Event.OnTeamSelectDismiss)
                    showTeamSheet = false
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    MoballTheme {
        MyPageScreen(
            state = MyPageContract.State(
                nickname = "닉네임",
                supportedTeams = listOf("한화", "KT", "삼성"),
            ),
            onEvent = {},
        )
    }
}
