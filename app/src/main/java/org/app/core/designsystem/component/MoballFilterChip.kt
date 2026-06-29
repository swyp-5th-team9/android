package org.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R.drawable
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

private val ChipShape = RoundedCornerShape(100.dp)

/**
 * @param label       표시할 라벨 (구단, 한화·엘지, 현재 위치, 강남 등)
 * @param onClick     클릭 콜백
 * @param modifier    외부 Modifier
 * @param isSelected  선택 상태 여부 (true → lime 테두리 표시)
 * @param leadingIcon 좌측 아이콘 (null이면 미표시)
 * @param showTrailingIcon 우측 화살표 아이콘 표시 여부
 */
@Composable
fun MoballFilterChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    leadingIcon: ImageVector? = null,
    showTrailingIcon: Boolean = true,
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 3.dp,
                shape = ChipShape,
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x14000000),
            ).then(
                if (isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = MoballTheme.colors.accentPrimary,
                        shape = ChipShape,
                    )
                } else {
                    Modifier
                },
            ).background(
                color = MoballTheme.colors.backgroundBase,
                shape = ChipShape,
            ).noRippleClickable(
                onClick = onClick,
            ).padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }

        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MoballTheme.typography.heading6.bold16,
                color = MoballTheme.colors.textPrimary,
                modifier = Modifier.padding(start = if (leadingIcon != null) 4.dp else 0.dp),
            )
        }

        if (showTrailingIcon) {
            Icon(
                imageVector = ImageVector.vectorResource(drawable.ic_arrow_down),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFBFC6CF)
@Composable
private fun MoballFilterChipDefaultPreview() {
    MoballTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MoballFilterChip(
                label = "구단",
                onClick = {},
                leadingIcon = ImageVector.vectorResource(drawable.ic_baseball_chip),
            )
            MoballFilterChip(
                label = "현재 위치",
                onClick = {},
                leadingIcon = ImageVector.vectorResource(drawable.ic_location),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFBFC6CF)
@Composable
private fun MoballFilterChipSelectedPreview() {
    MoballTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MoballFilterChip(
                label = "한화,엘지",
                onClick = {},
                isSelected = true,
                leadingIcon = ImageVector.vectorResource(drawable.ic_baseball_chip),
            )
            MoballFilterChip(
                label = "강남",
                onClick = {},
                isSelected = true,
                leadingIcon = ImageVector.vectorResource(drawable.ic_location),
            )
        }
    }
}
