package org.app.presentation.home.homesearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.home.component.SearchDetailTextField
import org.app.presentation.home.component.SearchListItemTextField
import org.app.presentation.home.model.PubSearchResult

@Composable
fun HomeSearchRoute(
    onBack: () -> Unit,
    onNavigateToPubDetail: (pubId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeSearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val textFieldState = rememberTextFieldState()

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .collect { viewModel.onEvent(HomeSearchContract.Event.OnQueryChange(it)) }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                HomeSearchContract.SideEffect.NavigateBack -> onBack()
                is HomeSearchContract.SideEffect.NavigateToPubDetail ->
                    onNavigateToPubDetail(effect.pubId)
            }
        }
    }

    HomeSearchScreen(
        state = state,
        textFieldState = textFieldState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
internal fun HomeSearchScreen(
    state: HomeSearchContract.State,
    textFieldState: TextFieldState,
    onEvent: (HomeSearchContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase)
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp),
    ) {
        SearchDetailTextField(
            state = textFieldState,
            onBack = { onEvent(HomeSearchContract.Event.OnBack) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "펍 이름, 구단을 입력해 주세요.",
        )

        HorizontalDivider(color = MoballTheme.colors.borderNormal)

        when {
            state.isEmpty -> {
                Text(
                    text = "검색 결과가 없습니다.",
                    style = MoballTheme.typography.body.regular14,
                    color = MoballTheme.colors.textTertiary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 48.dp),
                )
            }

            state.results.isNotEmpty() -> {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.results, key = { it.pubId }) { result ->
                        SearchListItemTextField(
                            text = result.name,
                            onClick = {
                                onEvent(HomeSearchContract.Event.OnResultClick(result.pubId))
                            },
                        )
                        HorizontalDivider(
                            color = MoballTheme.colors.borderNormal,
                            thickness = 1.dp,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchScreenWithResultsPreview() {
    MoballTheme {
        HomeSearchScreen(
            state = HomeSearchContract.State(
                results = listOf(
                    PubSearchResult("1", "시그니처 펍", "서울 마포구"),
                    PubSearchResult("2", "시그마 펍", "서울 강남구"),
                ),
            ),
            textFieldState = rememberTextFieldState("시그"),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchScreenEmptyPreview() {
    MoballTheme {
        HomeSearchScreen(
            state = HomeSearchContract.State(isEmpty = true),
            textFieldState = rememberTextFieldState("없는펍"),
            onEvent = {},
        )
    }
}
