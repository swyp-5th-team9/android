package org.app.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.moball.app.R.drawable
import com.moball.app.R.string.home
import com.moball.app.R.string.mypage
import com.moball.app.R.string.schedule
import org.app.core.common.navigation.MainTabRoute
import org.app.core.common.navigation.Route
import org.app.presentation.home.navigation.Home
import org.app.presentation.mypage.MyPage
import org.app.presentation.schedule.navigation.Schedule

enum class MainTab(
    @get:DrawableRes val selectedIconRes: Int,
    @get:DrawableRes val unselectedIconRes: Int,
    @get:StringRes val titleRes: Int,
    val route: MainTabRoute,
) {
    HOME(
        selectedIconRes = drawable.ic_home_selected_,
        unselectedIconRes = drawable.ic_home_unselected,
        titleRes = home,
        route = Home,
    ),

    SCHEDULE(
        selectedIconRes = drawable.ic_calender_selected,
        unselectedIconRes = drawable.ic_calender_unselected,
        titleRes = schedule,
        route = Schedule,
    ),

    MYPAGE(
        selectedIconRes = drawable.ic_my_selected,
        unselectedIconRes = drawable.ic_my_unselected,
        titleRes = mypage,
        route = MyPage,
    ),
    ;

    companion object {
        fun find(predicate: (MainTabRoute) -> Boolean): MainTab? = entries.find { predicate(it.route) }

        fun contains(predicate: (Route) -> Boolean): Boolean = entries.map { it.route }.any { predicate(it) }
    }
}
