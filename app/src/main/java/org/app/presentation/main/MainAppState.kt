package org.app.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.app.core.extension.stateInWhileSubscribed
import org.app.presentation.home.navigation.Home
import org.app.presentation.home.navigation.navigateToHome
import org.app.presentation.mypage.navigateToMyPage
import org.app.presentation.onboarding.login.navigation.Login
import org.app.presentation.schedule.navigation.navigateToSchedule

@Stable
class MainAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
) {
    val startDestination = Login

    private val currentDestination = navController.currentBackStackEntryFlow
        .map { it.destination }
        .stateInWhileSubscribed(
            scope = coroutineScope,
            initialValue = null,
        )

    val currentTab: StateFlow<MainTab?> = currentDestination
        .map { destination ->
            MainTab.entries.find { tab ->
                destination?.hasRoute(tab.route::class) == true
            }
        }.stateInWhileSubscribed(
            scope = coroutineScope,
            initialValue = null,
        )

    private val shouldShowBottomBar = MutableStateFlow(true)

    private val isMainTabRoute: StateFlow<Boolean> = currentDestination
        .map { destination ->
            MainTab.entries.any { tab ->
                destination?.hasRoute(tab.route::class) == true
            }
        }.stateInWhileSubscribed(
            scope = coroutineScope,
            initialValue = false,
        )

    val isBottomBarVisible: StateFlow<Boolean> = combine(
        isMainTabRoute,
        shouldShowBottomBar,
    ) { isMainTab, shouldShow ->
        isMainTab && shouldShow
    }.stateInWhileSubscribed(
        scope = coroutineScope,
        initialValue = false,
    )

    fun navigate(tab: MainTab) {
        val navOptions = navOptions {
            popUpTo<Home> {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            MainTab.HOME -> navController.navigateToHome(navOptions = navOptions)
            MainTab.SCHEDULE -> navController.navigateToSchedule(navOptions = navOptions)
            MainTab.MYPAGE -> navController.navigateToMyPage(navOptions = navOptions)
        }
    }

    fun updateBottomBarVisible(isVisible: Boolean) {
        shouldShowBottomBar.value = isVisible
    }
}

@Composable
fun rememberMainAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): MainAppState =
    remember(navController, coroutineScope) {
        MainAppState(navController, coroutineScope)
    }
