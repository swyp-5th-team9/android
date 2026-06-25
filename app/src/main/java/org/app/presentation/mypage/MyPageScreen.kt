package org.app.presentation.mypage

import android.content.ClipData
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import kotlinx.coroutines.launch
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.mypage.component.MyPageAddSportsCard
import org.app.presentation.mypage.component.MyPageProfileCard
import org.app.presentation.mypage.component.MyPageSettingCard
import org.app.presentation.mypage.component.MyPageSettingItem
import org.app.presentation.mypage.component.MyPageTeamSelectBottomSheet
import org.app.presentation.mypage.wishlist.WishlistItem
import org.app.presentation.mypage.wishlist.component.WishlistPreviewCard

// TODO 찜 목록 없을 때 디자인 수정 예정
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
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var showTeamSheet by remember { mutableStateOf(false) }

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
        isTeamSheetVisible = showTeamSheet,
        onEvent = viewModel::onEvent,
        onCopyEmail = { email ->
            scope.launch {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("email", email)))
                viewModel.onEvent(MyPageContract.Event.OnCopyEmailClick)
            }
        },
        onAddTeamClick = { showTeamSheet = true },
        onDismissTeamSheet = { showTeamSheet = false },
        modifier = modifier,
    )
}

@Composable
private fun MyPageScreen(
    state: MyPageContract.State,
    isTeamSheetVisible: Boolean,
    onEvent: (MyPageContract.Event) -> Unit,
    onCopyEmail: (String) -> Unit,
    onAddTeamClick: () -> Unit,
    onDismissTeamSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val email = "appswyp5th9team@gmail.com"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(state = TopBarState.Default(title = "마이페이지"))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 56.dp),
        ) {
            item {
                MyPageProfileCard(
                    nickname = state.nickname,
                    profileImageUrl = state.profileImageUrl,
                    onEditProfileClick = { onEvent(MyPageContract.Event.OnEditProfileClick) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                MyPageAddSportsCard(
                    supportedTeams = state.supportedTeams,
                    onAddClick = onAddTeamClick,
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (state.wishlistItems.isEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "펍 즐겨찾기 목록",
                            style = MoballTheme.typography.heading3.semibold20,
                            color = MoballTheme.colors.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "아직 즐겨찾기에 추가한 펍이 없어요",
                            style = MoballTheme.typography.body.regular14,
                            color = MoballTheme.colors.textTertiary,
                        )
                    }
                } else {
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

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.wishlistItems.take(5)) { item ->
                            WishlistPreviewCard(
                                pubName = item.pubName,
                                location = item.location,
                                imageUrl = item.imageUrl,
                            )
                        }
                    }
                }
            }

            item {
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
                            onClick = { // TODO 약관 및 정책 딥링크 연결
                            },
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCopyEmail(email) },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("문의: ")
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append(email)
                            }
                        },
                        style = MoballTheme.typography.caption.regular12,
                        color = MoballTheme.colors.textTertiary,
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = "복사",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "모여볼 v1.0.0",
                    style = MoballTheme.typography.caption.regular12,
                    color = MoballTheme.colors.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (isTeamSheetVisible) {
            MyPageTeamSelectBottomSheet(
                selectedTeams = state.supportedTeams,
                onTeamClick = { team -> onEvent(MyPageContract.Event.OnTeamSelected(team)) },
                onApply = onDismissTeamSheet,
                onDismiss = {
                    onEvent(MyPageContract.Event.OnTeamSelectDismiss)
                    onDismissTeamSheet()
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
            isTeamSheetVisible = false,
            onEvent = {},
            onCopyEmail = {},
            onAddTeamClick = {},
            onDismissTeamSheet = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenWithWishlistPreview() {
    MoballTheme {
        MyPageScreen(
            state = MyPageContract.State(
                nickname = "닉네임",
                supportedTeams = listOf("한화", "KT", "삼성"),
                wishlistItems = listOf(
                    WishlistItem("1", "버드나무 브루어리", "강릉", null),
                    WishlistItem("2", "데블스도어", "반포", null),
                    WishlistItem("3", "플레이볼", "잠실", null),
                ),
            ),
            isTeamSheetVisible = false,
            onEvent = {},
            onCopyEmail = {},
            onAddTeamClick = {},
            onDismissTeamSheet = {},
        )
    }
}
