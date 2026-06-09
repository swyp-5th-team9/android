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
import org.app.presentation.login.Login
import org.app.presentation.map.navigation.navigateToMap
import org.app.presentation.mypage.navigateToMyPage

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
            MainTab.find { tab ->
                destination?.hasRoute(tab::class) == true
            }
        }.stateInWhileSubscribed(
            scope = coroutineScope,
            initialValue = null,
        )

    private val shouldShowBottomBar = MutableStateFlow(true)

    private val isMainTabRoute: StateFlow<Boolean> = currentDestination
        .map { destination ->
            MainTab.contains { tab ->
                destination?.hasRoute(tab::class) == true
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
        // TODO: navigate 함수 추가 예정
        when (tab) {
            MainTab.HOME -> {
                navController.navigateToHome(navOptions = navOptions)
            }

            MainTab.MAP -> {
                navController.navigateToMap(navOptions = navOptions)
            }

            MainTab.MYPAGE -> {
                navController.navigateToMyPage(navOptions = navOptions)
            }

            MainTab.Dummy -> {}

            MainTab.Dummy1 -> {}

            MainTab.Dummy2 -> {}
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
