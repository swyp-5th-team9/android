package org.app.presentation.mypage.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme

// Figma Switch 컴포넌트 스펙: 트랙 80x36, 썸(원) 26, 수직 5, 가로 이동 6(off)→48(on)
private val TrackWidth = 80.dp
private val TrackHeight = 36.dp
private val ThumbSize = 26.dp
private val ThumbInsetVertical = 5.dp
private val ThumbOffsetOff = 6.dp
private val ThumbOffsetOn = 48.dp

/**
 * 모여볼 커스텀 토글 스위치.
 * - ON: 트랙 [MoballTheme.colors.iconPrimary](#3D4652)
 * - OFF: 트랙 [MoballTheme.colors.iconQuaternary](#BFC6CF)
 * - 썸은 흰색 원, 상태 전환 시 슬라이드 애니메이션.
 */
@Composable
fun NotificationSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) MoballTheme.colors.iconPrimary else MoballTheme.colors.iconQuaternary,
        label = "notificationSwitchTrack",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) ThumbOffsetOn else ThumbOffsetOff,
        label = "notificationSwitchThumb",
    )

    Box(
        modifier = modifier
            .size(width = TrackWidth, height = TrackHeight)
            .clip(RoundedCornerShape(20.dp))
            .background(trackColor)
            .toggleable(
                value = checked,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset, y = ThumbInsetVertical)
                .size(ThumbSize)
                .clip(CircleShape)
                .background(MoballTheme.colors.staticWhite),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationSwitchPreview() {
    MoballTheme {
        Box(modifier = Modifier.size(width = 100.dp, height = 100.dp)) {
            NotificationSwitch(checked = true, onCheckedChange = {})
        }
    }
}

@Preview(name = "OFF", showBackground = true)
@Composable
private fun NotificationSwitchOffPreview() {
    MoballTheme {
        Box(modifier = Modifier.size(width = 100.dp, height = 100.dp)) {
            NotificationSwitch(checked = false, onCheckedChange = {})
        }
    }
}
