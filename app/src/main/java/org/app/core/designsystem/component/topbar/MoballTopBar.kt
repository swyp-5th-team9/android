package org.app.core.designsystem.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.minTouchTarget
import org.app.core.extension.noRippleClickable

/**
 * 모여볼 기본 탑바 컴포넌트
 *
 * @param state 탑바 상태 ([TopBarState.Default], [TopBarState.Back], [TopBarState.Close], [TopBarState.BackWithSkip])
 * @param modifier 적용할 Modifier
 */
@Composable
fun MoballTopBar(
    state: TopBarState,
    modifier: Modifier = Modifier,
    centerContent: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .height(IntrinsicSize.Min)
            .background(MoballTheme.colors.staticWhite),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val onBackClick = when (state) {
                is TopBarState.Back -> state.onBackClick
                is TopBarState.BackWithSkip -> state.onBackClick
                is TopBarState.BackWithTextMenu -> state.onBackClick
                is TopBarState.BackWithOnboarding1 -> state.onBackClick
                else -> null
            }

            if (onBackClick != null) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_left),
                    contentDescription = null,
                    tint = MoballTheme.colors.iconPrimary,
                    modifier = Modifier
                        .minTouchTarget()
                        .noRippleClickable(onClick = onBackClick),
                )
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }

            if (centerContent == null && state.title.isNotEmpty()) {
                Text(
                    text = state.title,
                    style = MoballTheme.typography.heading2.semibold22,
                    color = MoballTheme.colors.textPrimary,
                    modifier = Modifier.padding(start = if (onBackClick != null) 4.dp else 0.dp),
                )
            }
        }

        if (centerContent != null) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center,
            ) {
                centerContent()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (state) {
                is TopBarState.Close -> {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                        contentDescription = null,
                        tint = MoballTheme.colors.iconPrimary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .minTouchTarget()
                            .noRippleClickable(onClick = state.onCloseClick),
                    )
                }

                is TopBarState.BackWithSkip -> {
                    Text(
                        text = "건너뛰기",
                        style = MoballTheme.typography.body.regular14,
                        color = MoballTheme.colors.textTertiary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .minTouchTarget()
                            .noRippleClickable(onClick = state.onSkipClick),
                    )
                }

                is TopBarState.BackWithTextMenu -> {
                    Text(
                        text = state.menuText,
                        style = MoballTheme.typography.body.medium14,
                        color = MoballTheme.colors.textSecondary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .minTouchTarget()
                            .noRippleClickable(onClick = state.onMenuClick),
                    )
                }

                else -> {}
            }
        }
    }
}

class TopBarStateProvider : PreviewParameterProvider<TopBarState> {
    override val values: Sequence<TopBarState> = sequenceOf(
        TopBarState.Default(title = "제목"),
        TopBarState.Back(title = "뒤로가기", onBackClick = {}),
        TopBarState.Close(title = "닫기", onCloseClick = {}),
        TopBarState.BackWithSkip(onBackClick = {}, onSkipClick = {}),
    )
}

@Preview(showBackground = true)
@Composable
private fun MoballTopBarPreview(
    @PreviewParameter(TopBarStateProvider::class) state: TopBarState,
) {
    MoballTheme {
        MoballTopBar(state = state)
    }
}
