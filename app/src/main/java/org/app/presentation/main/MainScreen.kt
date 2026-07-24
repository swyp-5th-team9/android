package org.app.presentation.main

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.collections.immutable.toImmutableList
import org.app.presentation.home.navigation.HomeGraph
import org.app.presentation.home.navigation.homeGraph
import org.app.presentation.home.pubfilter.navigation.PubFilter
import org.app.presentation.main.component.MainBottomBar
import org.app.presentation.mypage.myPageGraph
import org.app.presentation.onboarding.login.navigation.Login
import org.app.presentation.onboarding.login.navigation.loginGraph
import org.app.presentation.onboarding.signup.navigation.SignUpComplete
import org.app.presentation.onboarding.signup.navigation.SignUpNickname
import org.app.presentation.onboarding.signup.navigation.signUpGraph
import org.app.presentation.onboarding.splash.navigation.Splash
import org.app.presentation.onboarding.splash.navigation.splashGraph
import org.app.presentation.pubdetail.navigation.PubDetail
import org.app.presentation.pubdetail.navigation.navigateToPubDetail
import org.app.presentation.pubdetail.navigation.pubDetailGraph
import org.app.presentation.schedule.navigation.scheduleGraph

private val fullBleedRoutePrefixes = listOfNotNull(
    Splash::class.qualifiedName,
    Login::class.qualifiedName,
    SignUpComplete::class.qualifiedName,
)

private val FullBleedContainerColor = Color(0xFF1C1C1C)

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

private const val FULL_BLEED_FADE_MS = 280

private fun AnimatedContentTransitionScope<NavBackStackEntry>.involvesFullBleed(): Boolean =
    listOf(initialState, targetState).any { entry ->
        entry.destination.route?.let { route ->
            fullBleedRoutePrefixes.any { route.startsWith(it) }
        } == true
    }

@Composable
fun MainScreen(
    appState: MainAppState,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val isBottomBarVisible by appState.isBottomBarVisible.collectAsStateWithLifecycle()
    val currentTab by appState.currentTab.collectAsStateWithLifecycle()

    val visibleEntries by appState.navController.visibleEntries.collectAsStateWithLifecycle()
    val isDarkFullBleed = visibleEntries.any { entry ->
        entry.destination.route?.let { route ->
            fullBleedRoutePrefixes.any { route.startsWith(it) }
        } == true
    }

    val view = LocalView.current
    LaunchedEffect(isDarkFullBleed) {
        val window = view.context.findActivity()?.window ?: return@LaunchedEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkFullBleed
    }

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
        containerColor = if (isDarkFullBleed) FullBleedContainerColor else Color.White,
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
    val currentEntry by appState.navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val isFullBleed = currentRoute != null && fullBleedRoutePrefixes.any { currentRoute.startsWith(it) }

    val managesOwnBottomInset = currentRoute != null &&
        listOfNotNull(
            PubDetail::class.qualifiedName,
            PubFilter::class.qualifiedName,
        ).any { currentRoute.startsWith(it) }

    val contentModifier = when {
        isFullBleed -> Modifier.fillMaxSize()
        managesOwnBottomInset -> Modifier.padding(top = innerPadding.calculateTopPadding())
        else -> Modifier.padding(innerPadding)
    }

    NavHost(
        enterTransition = { if (involvesFullBleed()) fadeIn(tween(FULL_BLEED_FADE_MS)) else EnterTransition.None },
        exitTransition = { if (involvesFullBleed()) fadeOut(tween(FULL_BLEED_FADE_MS)) else ExitTransition.None },
        popEnterTransition = { if (involvesFullBleed()) fadeIn(tween(FULL_BLEED_FADE_MS)) else EnterTransition.None },
        popExitTransition = { if (involvesFullBleed()) fadeOut(tween(FULL_BLEED_FADE_MS)) else ExitTransition.None },
        navController = appState.navController,
        startDestination = appState.startDestination,
        modifier = contentModifier,
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
