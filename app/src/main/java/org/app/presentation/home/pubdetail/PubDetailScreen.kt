package org.app.presentation.home.pubdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun PubDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: PubDetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            // SideEffect 처리
        }
    }

    PubDetailScreen(
        modifier = modifier,
    )
}

@Composable
internal fun PubDetailScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Pub Detail Screen")
    }
}
