package org.app.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import org.app.core.common.util.CollectSideEffect
import org.app.core.designsystem.component.LocalMoballToastHostState
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.notification.component.NotificationDeleteBottomSheet
import org.app.presentation.notification.component.NotificationItemCard

@Composable
fun NotificationRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val toastHostState = LocalMoballToastHostState.current
    var deleteTargetId by rememberSaveable { mutableStateOf<Long?>(null) }

    CollectSideEffect(viewModel.sideEffect) { effect ->
        when (effect) {
            NotificationContract.SideEffect.NavigateBack -> onBack()
            is NotificationContract.SideEffect.ShowToast -> toastHostState.show(effect.message)
        }
    }

    NotificationScreen(
        state = state,
        onBackClick = { viewModel.onEvent(NotificationContract.Event.OnBackClick) },
        onDeleteRequest = { deleteTargetId = it },
        modifier = modifier,
    )

    deleteTargetId?.let { id ->
        NotificationDeleteBottomSheet(
            onConfirm = {
                viewModel.onEvent(NotificationContract.Event.OnDeleteClick(id))
                deleteTargetId = null
            },
            onDismiss = { deleteTargetId = null },
        )
    }
}

@Composable
private fun NotificationScreen(
    state: NotificationContract.State,
    onBackClick: () -> Unit,
    onDeleteRequest: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MoballTopBar(state = TopBarState.Back(title = "알림", onBackClick = onBackClick))

            if (state.isEmpty) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "아직 받은 알림이 없어요",
                        style = MoballTheme.typography.body.regular14,
                        color = MoballTheme.colors.textTertiary,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.items, key = { it.id }) { item ->
                        NotificationItemCard(
                            item = item,
                            onMoreClick = { onDeleteRequest(item.id) },
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    MoballTheme {
        NotificationScreen(
            state = NotificationContract.State(
                items = persistentListOf(
                    NotificationItem(
                        id = 1L,
                        title = "경기 일정 알림",
                        message = "LG 트윈스 경기가 오늘 오후 6시에 있어요!",
                        date = "8월 15일",
                    ),
                    NotificationItem(
                        id = 2L,
                        title = "경기 시작 알림",
                        message = "잠시 후 6시 30분에 경기가 시작돼요. 상영 펍을 확인해보세요!",
                        date = "8월 14일",
                    ),
                ),
            ),
            onBackClick = {},
            onDeleteRequest = {},
        )
    }
}

@Preview(name = "빈 상태", showBackground = true)
@Composable
private fun NotificationScreenEmptyPreview() {
    MoballTheme {
        NotificationScreen(
            state = NotificationContract.State(items = persistentListOf()),
            onBackClick = {},
            onDeleteRequest = {},
        )
    }
}
