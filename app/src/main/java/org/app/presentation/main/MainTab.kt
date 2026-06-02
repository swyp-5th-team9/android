package org.app.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.app.R.drawable.ic_launcher_background
import org.app.R.string.home
import org.app.core.common.navigation.MainTabRoute
import org.app.core.common.navigation.Route
import org.app.presentation.home.navigation.Home

enum class MainTab(
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val route: MainTabRoute,
) {
    HOME(
        iconRes = ic_launcher_background,
        titleRes = home,
        route = Home,
    ),

    // TODO: 추후 변경 예정
    Dummy(
        iconRes = ic_launcher_background,
        titleRes = home,
        route = Home,
    ),
    Dummy1(
        iconRes = ic_launcher_background,
        titleRes = home,
        route = Home,
    ),
    Dummy2(
        iconRes = ic_launcher_background,
        titleRes = home,
        route = Home,
    ),
    ;

    companion object {
        fun find(predicate: (MainTabRoute) -> Boolean): MainTab? = entries.find { predicate(it.route) }

        fun contains(predicate: (Route) -> Boolean): Boolean = entries.map { it.route }.any { predicate(it) }
    }
}
