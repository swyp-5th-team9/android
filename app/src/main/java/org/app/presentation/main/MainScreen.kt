package org.app.presentation.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import kotlinx.collections.immutable.toImmutableList
import org.app.presentation.home.navigation.Home
import org.app.presentation.home.navigation.homeGraph
import org.app.presentation.home.pubdetail.navigation.navigateToPubDetail
import org.app.presentation.home.pubdetail.navigation.pubDetailGraph
import org.app.presentation.main.component.MainBottomBar
import org.app.presentation.mypage.myPageGraph
import org.app.presentation.onboarding.login.navigation.Login
import org.app.presentation.onboarding.login.navigation.loginGraph
import org.app.presentation.schedule.navigation.scheduleGraph

@Composable
fun MainScreen(appState: MainAppState) {
    val isBottomBarVisible by appState.isBottomBarVisible.collectAsStateWithLifecycle()
    val currentTab by appState.currentTab.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            MainBottomBar(
                isVisible = isBottomBarVisible,
                tabs = MainTab.entries.toImmutableList(),
                currentTab = currentTab,
                onTabSelected = appState::navigate,
            )
        },
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        MainNavHost(
            appState = appState,
            innerPadding = innerPadding,
        )
    }
}

@Composable
private fun MainNavHost(
    appState: MainAppState,
    innerPadding: PaddingValues,
) {
    NavHost(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        navController = appState.navController,
        startDestination = appState.startDestination,
        modifier = Modifier.padding(innerPadding),
    ) {
        loginGraph(
            navigateToHome = {
                appState.navController.navigate(Home) {
                    popUpTo(appState.navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )
        homeGraph(
            navigateToPubDetail = {
                appState.navController.navigateToPubDetail()
            },
        )
        scheduleGraph()
        pubDetailGraph()
        myPageGraph(
            navigateToLogin = {
                appState.navController.navigate(Login) {
                    popUpTo(appState.navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
    }
}
