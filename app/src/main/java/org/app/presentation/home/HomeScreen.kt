package org.app.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
    }
    HomeScreen(
        modifier = modifier,
    )
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {
}

@Preview(showBackground = true)
@Composable
private fun DummyScreenPreview() {
}
