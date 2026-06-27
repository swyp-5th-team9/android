package org.app.presentation.mypage.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import org.app.core.designsystem.component.MoballDialog
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.mypage.wishlist.component.WishlistEditButton
import org.app.presentation.mypage.wishlist.component.WishlistItemCard

@Composable
fun WishlistRoute(
    onBack: () -> Unit,
    navigateToPubDetail: (pubId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WishlistViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                WishlistContract.SideEffect.NavigateBack -> onBack()
                is WishlistContract.SideEffect.NavigateToPubDetail -> navigateToPubDetail(effect.pubId)
                is WishlistContract.SideEffect.ShowToast -> {
                    android.widget.Toast
                        .makeText(context, effect.message, android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    WishlistScreen(
        state = state,
        onBack = onBack,
        onEditClick = { viewModel.onEvent(WishlistContract.Event.OnEditClick) },
        onCancelEdit = { viewModel.onEvent(WishlistContract.Event.OnCancelEdit) },
        onDeleteClick = { showDeleteDialog = true },
        onCardClick = { pubId -> viewModel.onEvent(WishlistContract.Event.OnPubClick(pubId)) },
        onHeartClick = { pubId -> viewModel.onEvent(WishlistContract.Event.OnToggleFavorite(pubId)) },
        modifier = modifier,
    )

    if (showDeleteDialog) {
        MoballDialog(
            title = "펍 즐겨찾기 삭제",
            subtitle = "즐겨찾기 목록에서 삭제할까요?",
            onConfirm = {
                viewModel.onEvent(WishlistContract.Event.OnDeleteSelected)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
            iconRes = R.drawable.ic_trash_full,
        )
    }
}

@Composable
private fun WishlistScreen(
    state: WishlistContract.State,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onCancelEdit: () -> Unit,
    onDeleteClick: () -> Unit,
    onCardClick: (pubId: String) -> Unit,
    onHeartClick: (pubId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        Column {
            MoballTopBar(
                state = TopBarState.BackWithTextMenu(
                    title = "찜 목록",
                    menuText = if (state.isEditMode) "완료" else "편집",
                    onBackClick = onBack,
                    onMenuClick = onEditClick,
                ),
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = if (state.isEditMode) 106.dp else 20.dp,
                ),
            ) {
                item {
                    Text(
                        text = "최대 30개까지 등록 가능합니다.",
                        style = MoballTheme.typography.caption.medium12,
                        color = MoballTheme.colors.textTertiary,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }

                items(state.items.chunked(3)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        rowItems.forEach { item ->
                            WishlistItemCard(
                                item = item,
                                isEditMode = state.isEditMode,
                                isSelected = item.pubId in state.selectedIds,
                                onCardClick = { onCardClick(item.pubId) },
                                onHeartClick = { onHeartClick(item.pubId) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (state.isEditMode) {
            WishlistEditButton(
                hasSelection = state.hasSelection,
                onCancel = onCancelEdit,
                onDelete = onDeleteClick,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WishlistScreenPreview() {
    MoballTheme {
        WishlistScreen(
            state = WishlistContract.State(
                items = listOf(
                    WishlistItem("1", "펍 이름 1", "홍대", null),
                    WishlistItem("2", "펍 이름 2", "강남", null),
                    WishlistItem("3", "펍 이름 3", "이태원", null),
                    WishlistItem("4", "펍 이름 4", "건대", null),
                    WishlistItem("5", "펍 이름 5", "신촌", null),
                ),
            ),
            onBack = {},
            onEditClick = {},
            onCancelEdit = {},
            onDeleteClick = {},
            onCardClick = {},
            onHeartClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WishlistScreenEditPreview() {
    MoballTheme {
        WishlistScreen(
            state = WishlistContract.State(
                items = listOf(
                    WishlistItem("1", "펍 이름 1", "홍대", null),
                    WishlistItem("2", "펍 이름 2", "강남", null),
                    WishlistItem("3", "펍 이름 3", "이태원", null),
                ),
                isEditMode = true,
                selectedIds = setOf("1"),
            ),
            onBack = {},
            onEditClick = {},
            onCancelEdit = {},
            onDeleteClick = {},
            onCardClick = {},
            onHeartClick = {},
        )
    }
}
