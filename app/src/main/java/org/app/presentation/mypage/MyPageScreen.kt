package org.app.presentation.mypage

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.BuildConfig
import com.moball.app.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.app.core.common.util.CollectSideEffect
import org.app.core.designsystem.component.LocalMoballToastHostState
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.mypage.component.MyPageAddSportsCard
import org.app.presentation.mypage.component.MyPageFavoriteSection
import org.app.presentation.mypage.component.MyPageProfileCard
import org.app.presentation.mypage.component.MyPageSettingCard
import org.app.presentation.mypage.component.MyPageSettingItem
import org.app.presentation.mypage.component.MyPageTeamSelectBottomSheet
import org.app.presentation.mypage.favorite.FavoritePubItem

@Composable
fun MyPageRoute(
    navigateToLogin: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToReport: () -> Unit,
    navigateToWithdraw: () -> Unit,
    navigateToFavorite: () -> Unit,
    navigateToPubDetail: (pubId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val toastHostState = LocalMoballToastHostState.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var showTeamSheet by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(MyPageContract.Event.OnRefresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    CollectSideEffect(viewModel.sideEffect) { effect ->
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

            MyPageContract.SideEffect.NavigateToFavorite -> {
                navigateToFavorite()
            }

            is MyPageContract.SideEffect.NavigateToPubDetail -> {
                navigateToPubDetail(effect.pubId)
            }

            is MyPageContract.SideEffect.ShowToast -> {
                toastHostState.show(effect.message)
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
    val uriHandler = LocalUriHandler.current
    val email = "moyeoball@gmail.com"
    val privacyPolicyUrl =
        "https://puzzle-visor-003.notion.site/390b8196eb4880b881f7f5641e7fc72f?source=copy_link"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(state = TopBarState.Default(title = "마이페이지"))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 56.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    MyPageProfileCard(
                        nickname = state.nickname,
                        profileImageUrl = state.profileImageUrl,
                        onEditProfileClick = { onEvent(MyPageContract.Event.OnEditProfileClick) },
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "응원 구단",
                        style = MoballTheme.typography.heading3.semibold20,
                        color = MoballTheme.colors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    MyPageAddSportsCard(
                        supportedTeams = state.supportedTeams,
                        onAddClick = onAddTeamClick,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                MyPageFavoriteSection(
                    favoriteItems = state.favoriteItems,
                    onFavoriteClick = { onEvent(MyPageContract.Event.OnFavoriteClick) },
                    onPubClick = { pubId -> onEvent(MyPageContract.Event.OnPubClick(pubId)) },
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
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
                                iconRes = R.drawable.ic_sound,
                                title = "제보하기",
                                subtitle = "잘못된 정보, 앱 오류 신고",
                                onClick = { onEvent(MyPageContract.Event.OnReportClick) },
                            ),
                            MyPageSettingItem(
                                iconRes = R.drawable.ic_warning_info,
                                title = "약관 및 정책",
                                subtitle = "개인정보처리방침",
                                onClick = { uriHandler.openUri(privacyPolicyUrl) },
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
                            .noRippleClickable { onEvent(MyPageContract.Event.OnWithdrawClick) },
                        textDecoration = TextDecoration.Underline,
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable { onCopyEmail(email) },
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
                        text = "모여볼 v${BuildConfig.VERSION_NAME}",
                        style = MoballTheme.typography.caption.regular12,
                        color = MoballTheme.colors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        if (isTeamSheetVisible) {
            MyPageTeamSelectBottomSheet(
                initialSelectedTeams = state.supportedTeams,
                onApply = { teams ->
                    onEvent(MyPageContract.Event.OnApplyTeams(teams))
                    onDismissTeamSheet()
                },
                onDismiss = onDismissTeamSheet,
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
                supportedTeams = persistentListOf("한화", "KT", "삼성"),
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
private fun MyPageScreenWithFavoritePreview() {
    MoballTheme {
        MyPageScreen(
            state = MyPageContract.State(
                nickname = "닉네임",
                supportedTeams = persistentListOf("한화", "KT", "삼성"),
                favoriteItems = persistentListOf(
                    FavoritePubItem(favoriteId = 1L, pubId = 11L, pubName = "버드나무 브루어리", address = "강릉"),
                    FavoritePubItem(favoriteId = 2L, pubId = 12L, pubName = "데블스도어", address = "고속터미널"),
                    FavoritePubItem(favoriteId = 3L, pubId = 13L, pubName = "플레이볼", address = "홍대"),
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
