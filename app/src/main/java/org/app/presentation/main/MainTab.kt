package org.app.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.moball.app.R.drawable.ic_launcher_background
import com.moball.app.R.string.home
import com.moball.app.R.string.map
import org.app.core.common.navigation.MainTabRoute
import org.app.core.common.navigation.Route
import org.app.presentation.home.navigation.Home
import org.app.presentation.map.navigation.Map

enum class MainTab(
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val route: MainTabRoute,
) {
    // TODO: 추후 변경 예정

    HOME(
        iconRes = ic_launcher_background,
        titleRes = home,
        route = Home,
    ),

    MAP(
        iconRes = ic_launcher_background,
        titleRes = map,
        route = Map,
    ),
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
