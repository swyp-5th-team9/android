package org.app.presentation.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.main.MainTab

@Composable
fun MainBottomBar(
    isVisible: Boolean,
    tabs: ImmutableList<MainTab>,
    currentTab: MainTab?,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideIn { IntOffset(0, it.height) },
        exit = fadeOut() + slideOut { IntOffset(0, it.height) },
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFF31353B))
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    key(tab.route) {
                        MainBottomBarItem(
                            tab = tab,
                            isSelected = tab == currentTab,
                            onClick = { onTabSelected(tab) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainBottomBarItem(
    tab: MainTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (textColor, iconColor, iconRes) = when {
        isSelected -> Triple(
            MoballTheme.colors.staticWhite,
            MoballTheme.colors.accentPrimary,
            tab.selectedIconRes,
        )

        else -> Triple(
            MoballTheme.colors.textTertiary,
            MoballTheme.colors.iconQuaternary,
            tab.unselectedIconRes,
        )
    }

    Column(
        modifier = modifier.noRippleClickable(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = stringResource(tab.titleRes),
            tint = iconColor,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = stringResource(tab.titleRes),
            style = MoballTheme.typography.heading6.bold16,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainBottomBarPreview() {
    MoballTheme {
        MainBottomBar(
            isVisible = true,
            tabs = persistentListOf(MainTab.HOME, MainTab.SCHEDULE, MainTab.MYPAGE),
            currentTab = MainTab.HOME,
            onTabSelected = {},
        )
    }
}
