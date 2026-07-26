package org.app.presentation.mypage.favorite

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.app.core.common.util.CollectSideEffect
import org.app.core.designsystem.component.LocalMoballToastHostState
import org.app.core.designsystem.component.MoballDialog
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.mypage.favorite.component.FavoriteEditButton
import org.app.presentation.mypage.favorite.component.FavoriteItemCard

@Composable
fun FavoriteRoute(
    onBack: () -> Unit,
    navigateToPubDetail: (pubId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var heartDeleteId by remember { mutableStateOf<Long?>(null) }
    val toastHostState = LocalMoballToastHostState.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(FavoriteContract.Event.OnRefresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    CollectSideEffect(viewModel.sideEffect) { effect ->
        when (effect) {
            FavoriteContract.SideEffect.NavigateBack -> onBack()
            is FavoriteContract.SideEffect.NavigateToPubDetail -> navigateToPubDetail(effect.pubId)
            is FavoriteContract.SideEffect.ShowToast -> {
                toastHostState.show(effect.message)
            }
        }
    }

    FavoriteScreen(
        state = state,
        onBack = onBack,
        onEditClick = { viewModel.onEvent(FavoriteContract.Event.OnEditClick) },
        onCancelEdit = { viewModel.onEvent(FavoriteContract.Event.OnCancelEdit) },
        onDeleteClick = { showDeleteDialog = true },
        onCardClick = { pubId -> viewModel.onEvent(FavoriteContract.Event.OnPubClick(pubId)) },
        onHeartClick = { favoriteId -> heartDeleteId = favoriteId },
        modifier = modifier,
    )

    if (showDeleteDialog) {
        MoballDialog(
            title = "펍 즐겨찾기 삭제",
            subtitle = "즐겨찾기 목록에서 삭제할까요?",
            onConfirm = {
                viewModel.onEvent(FavoriteContract.Event.OnDeleteSelected)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
            iconRes = R.drawable.ic_trash_full,
        )
    }

    heartDeleteId?.let { favoriteId ->
        MoballDialog(
            title = "펍 즐겨찾기 삭제",
            subtitle = "즐겨찾기 목록에서 삭제할까요?",
            onConfirm = {
                viewModel.onEvent(FavoriteContract.Event.OnHeartClick(favoriteId))
                heartDeleteId = null
            },
            onDismiss = { heartDeleteId = null },
            iconRes = R.drawable.ic_trash_full,
        )
    }
}

@Composable
private fun FavoriteScreen(
    state: FavoriteContract.State,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onCancelEdit: () -> Unit,
    onDeleteClick: () -> Unit,
    onCardClick: (pubId: Long) -> Unit,
    onHeartClick: (favoriteId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        Column {
            MoballTopBar(
                state = if (state.items.isEmpty()) {
                    TopBarState.Back(title = "펍 즐겨찾기 목록", onBackClick = onBack)
                } else {
                    TopBarState.BackWithTextMenu(
                        title = "펍 즐겨찾기 목록",
                        menuText = if (state.isEditMode) "완료" else "편집",
                        onBackClick = onBack,
                        onMenuClick = onEditClick,
                    )
                },
            )

            if (state.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "아직 즐겨찾기한 펍이 없습니다.",
                        style = MoballTheme.typography.body.regular14,
                        color = MoballTheme.colors.textTertiary,
                    )
                }
            } else {
                val rows = remember(state.items) { state.items.chunked(3) }
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

                    items(rows, key = { row -> row.first().favoriteId }) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            rowItems.forEach { item ->
                                FavoriteItemCard(
                                    item = item,
                                    isEditMode = state.isEditMode,
                                    isSelected = item.favoriteId in state.selectedIds,
                                    onCardClick = { onCardClick(item.pubId) },
                                    onHeartClick = { onHeartClick(item.favoriteId) },
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
        }

        if (state.isEditMode) {
            FavoriteEditButton(
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
private fun FavoriteScreenEmptyPreview() {
    MoballTheme {
        FavoriteScreen(
            state = FavoriteContract.State(items = persistentListOf()),
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
private fun FavoriteScreenPreview() {
    MoballTheme {
        FavoriteScreen(
            state = FavoriteContract.State(
                items = persistentListOf(
                    FavoritePubItem(favoriteId = 1L, pubId = 11L, pubName = "펍 이름 1", address = "강남"),
                    FavoritePubItem(favoriteId = 2L, pubId = 12L, pubName = "펍 이름 2", address = "홍대"),
                    FavoritePubItem(favoriteId = 3L, pubId = 13L, pubName = "펍 이름 3", address = "잠실"),
                    FavoritePubItem(favoriteId = 4L, pubId = 14L, pubName = "펍 이름 4", address = "서초"),
                    FavoritePubItem(favoriteId = 5L, pubId = 15L, pubName = "펍 이름 5", address = "이태원"),
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
private fun FavoriteScreenEditPreview() {
    MoballTheme {
        FavoriteScreen(
            state = FavoriteContract.State(
                items = persistentListOf(
                    FavoritePubItem(favoriteId = 1L, pubId = 11L, pubName = "펍 이름 1", address = "강남"),
                    FavoritePubItem(favoriteId = 2L, pubId = 12L, pubName = "펍 이름 2", address = "홍대"),
                    FavoritePubItem(favoriteId = 3L, pubId = 13L, pubName = "펍 이름 3", address = "잠실"),
                ),
                isEditMode = true,
                selectedIds = persistentSetOf(1L),
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
