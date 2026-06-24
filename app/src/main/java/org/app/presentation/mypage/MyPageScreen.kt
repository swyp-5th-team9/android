package org.app.presentation.mypage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import org.app.presentation.mypage.component.WishlistPreviewCard

@Composable
fun MyPageRoute(
    navigateToLogin: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToReport: () -> Unit,
    navigateToWithdraw: () -> Unit,
    navigateToWishlist: () -> Unit,
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

                MyPageContract.SideEffect.NavigateToWishlist -> {
                    navigateToWishlist()
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "찜 목록",
                    style = MoballTheme.typography.heading3.semibold20,
                    color = MoballTheme.colors.textPrimary,
                )
                Text(
                    text = "전체보기",
                    style = MoballTheme.typography.body.regular14,
                    color = MoballTheme.colors.textTertiary,
                    modifier = Modifier.clickable { onEvent(MyPageContract.Event.OnWishlistClick) },
                )
            }

            if (state.wishlistItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.wishlistItems) { item ->
                        WishlistPreviewCard(
                            pubName = item.pubName,
                            location = item.location,
                            imageUrl = item.imageUrl,
                        )
                    }
                }
            }

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
                        iconRes = R.drawable.ic_headphones,
                        title = "제보하기",
                        subtitle = "잘못된 정보, 앱 오류 신고",
                        onClick = { onEvent(MyPageContract.Event.OnReportClick) },
                    ),
                    MyPageSettingItem(
                        iconRes = R.drawable.ic_lock,
                        title = "약관 및 정책",
                        subtitle = "이용약관 · 개인정보처리방침",
                        onClick = { onEvent(MyPageContract.Event.OnLogoutClick) },
                    ),
                    MyPageSettingItem(
                        iconRes = R.drawable.ic_logout,
                        title = "로그아웃",
                        onClick = { onEvent(MyPageContract.Event.OnLogoutClick) },
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
            Text(
                text = "문의: mmmmm@gmail.com",
                style = MoballTheme.typography.caption.regular12,
                color = MoballTheme.colors.textTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "버전 정보 v1.0.0",
                style = MoballTheme.typography.caption.regular12,
                color = MoballTheme.colors.textTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 56.dp),
            )
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
