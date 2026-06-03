package org.app.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.app.core.common.state.UiState
import org.app.data.model.DummyUser
import org.app.presentation.home.component.DummyUserItem

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchDummyUsers()
    }
    HomeScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeContract.State,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (uiState.dummyUsersLoadState) {
            is UiState.Idle -> {
                // 빈 상태 화면
            }

            is UiState.Failure -> {
                // 에러 상태 화면
            }

            is UiState.Loading -> {
                // 로딩 상태 화면
            }

            is UiState.Success -> {
                DummyUserListColumn(
                    dummyUsers = uiState.dummyUsersLoadState.data,
                    modifier = Modifier,
                )
            }
        }
    }
}

@Composable
private fun DummyUserListColumn(
    dummyUsers: ImmutableList<DummyUser>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        this.items(
            dummyUsers,
            key = { it.id },
        ) { user ->
            DummyUserItem(
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                profileImage = user.profileImage,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DummyScreenPreview() {
    DummyUserListColumn(
        dummyUsers = persistentListOf(),
    )
}
