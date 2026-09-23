package org.app.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
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
import org.app.presentation.onboarding.splash.navigation.Splash
import org.app.presentation.schedule.navigation.navigateToSchedule

@Stable
class MainAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
) {
    val startDestination: Any = Splash

    private val currentDestination = navController.currentBackStackEntryFlow
        .map { it.destination }
        .stateInWhileSubscribed(
            scope = coroutineScope,
            initialValue = null,
        )

    val currentTab: StateFlow<MainTab?> = currentDestination
        .map { destination ->
            MainTab.entries.find { tab ->
                destination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
            }
        }.stateInWhileSubscribed(
            scope = coroutineScope,
            initialValue = null,
        )

    private val shouldShowBottomBar = MutableStateFlow(true)

    private val isMainTabRoute: StateFlow<Boolean> = currentDestination
        .map { destination ->
            MainTab.entries.any { tab ->
                destination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
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

    /**
     * 알림 딥링크: 홈 탭으로 이동한 뒤, 홈이 관찰하는 savedStateHandle에 펍 필터를 전달한다.
     * (펍 필터 화면이 결과를 넘기는 것과 동일 메커니즘 — 인앱 알림 카드 탭/FCM 푸시 탭 공용)
     */
    fun applyPubDeepLink(
        teamIds: List<Long>,
        businessDay: String?,
    ) {
        navigate(MainTab.HOME)
        navController.getBackStackEntry(Home).savedStateHandle.apply {
            set("pub_filter_team_ids", ArrayList(teamIds))
            set("pub_filter_team_names", ArrayList<String>())
            set("pub_filter_regions", ArrayList<String>())
            set("pub_filter_open_now", false)
            set("pub_filter_business_day", businessDay)
            set("pub_filter_facility_codes", ArrayList<String>())
            set("pub_filter_style_codes", ArrayList<String>())
            set("pub_filter_theme_codes", ArrayList<String>())
            set("pub_filter_food_codes", ArrayList<String>())
            set("pub_filter_applied", true)
        }
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
