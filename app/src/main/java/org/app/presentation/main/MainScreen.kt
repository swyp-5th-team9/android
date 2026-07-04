package org.app.presentation.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import kotlinx.collections.immutable.toImmutableList
import org.app.presentation.home.navigation.HomeGraph
import org.app.presentation.home.navigation.homeGraph
import org.app.presentation.main.component.MainBottomBar
import org.app.presentation.mypage.myPageGraph
import org.app.presentation.onboarding.login.navigation.Login
import org.app.presentation.onboarding.login.navigation.loginGraph
import org.app.presentation.onboarding.signup.navigation.SignUpNickname
import org.app.presentation.onboarding.signup.navigation.signUpGraph
import org.app.presentation.onboarding.splash.navigation.Splash
import org.app.presentation.onboarding.splash.navigation.splashGraph
import org.app.presentation.pubdetail.navigation.navigateToPubDetail
import org.app.presentation.pubdetail.navigation.pubDetailGraph
import org.app.presentation.schedule.navigation.scheduleGraph

@Composable
fun MainScreen(
    appState: MainAppState,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val isBottomBarVisible by appState.isBottomBarVisible.collectAsStateWithLifecycle()
    val currentTab by appState.currentTab.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.authEvent.collect {
            appState.navController.navigate(Login) {
                popUpTo(appState.navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

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
        splashGraph(
            navigateToHome = {
                appState.navController.navigate(HomeGraph) {
                    popUpTo<Splash> { inclusive = true }
                    launchSingleTop = true
                }
            },
            navigateToLogin = {
                appState.navController.navigate(Login) {
                    popUpTo<Splash> { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        loginGraph(
            navigateToHome = {
                appState.navController.navigate(HomeGraph) {
                    popUpTo(appState.navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            navigateToSignUp = {
                appState.navController.navigate(SignUpNickname) {
                    launchSingleTop = true
                }
            },
        )
        homeGraph(
            navController = appState.navController,
            navigateToPubDetail = { pubId ->
                appState.navController.navigateToPubDetail(pubId = pubId)
            },
            onUpdateBottomBarVisible = appState::updateBottomBarVisible,
        )
        signUpGraph(
            navController = appState.navController,
            navigateToHome = {
                appState.navController.navigate(HomeGraph) {
                    popUpTo(appState.navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        scheduleGraph()
        pubDetailGraph(
            onBack = { appState.navController.popBackStack() },
        )
        myPageGraph(
            navController = appState.navController,
            navigateToLogin = {
                appState.navController.navigate(Login) {
                    popUpTo(appState.navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
    }
}
