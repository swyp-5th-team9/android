package org.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun MoballDialog(
    title: String,
    subtitle: String,
    onConfirm: () -> Unit,
    confirmText: String = "확인",
    onDismiss: (() -> Unit)? = null,
    dismissText: String = "취소",
    iconRes: Int? = null,
    iconVector: ImageVector? = null,
) {
    Dialog(
        onDismissRequest = { onDismiss?.invoke() ?: onConfirm() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        MoballDialogContent(
            title = title,
            subtitle = subtitle,
            confirmText = confirmText,
            onConfirm = onConfirm,
            dismissText = dismissText,
            onDismiss = onDismiss,
            iconRes = iconRes,
            iconVector = iconVector,
        )
    }
}

@Composable
private fun MoballDialogContent(
    title: String,
    subtitle: String,
    onConfirm: () -> Unit,
    confirmText: String,
    onDismiss: (() -> Unit)?,
    dismissText: String,
    iconRes: Int?,
    iconVector: ImageVector?,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 343.dp)
            .fillMaxWidth()
            .background(
                color = MoballTheme.colors.backgroundBase,
                shape = RoundedCornerShape(16.dp),
            ).padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(39.dp))

        if (iconRes != null || iconVector != null) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        color = MoballTheme.colors.textPrimary,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                val vector = iconVector ?: iconRes?.let { ImageVector.vectorResource(id = it) }
                if (vector != null) {
                    Icon(
                        imageVector = vector,
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        Text(
            text = title,
            style = MoballTheme.typography.heading2.bold22,
            color = MoballTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MoballTheme.typography.heading6.semibold16,
            color = MoballTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (onDismiss != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MoballTheme.colors.backgroundSurface,
                            shape = RoundedCornerShape(12.dp),
                        ).noRippleClickable(onClick = onDismiss)
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = dismissText,
                        style = MoballTheme.typography.heading5.semibold18,
                        color = MoballTheme.colors.textPrimary,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = MoballTheme.colors.accentPrimary,
                        shape = RoundedCornerShape(12.dp),
                    ).noRippleClickable(onClick = onConfirm)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = confirmText,
                    style = MoballTheme.typography.heading5.semibold18,
                    color = MoballTheme.colors.textPrimary,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "확인 단일 버튼")
@Composable
private fun MoballDialogPreview() {
    MoballTheme {
        MoballDialogContent(
            title = "제보가 접수됐어요",
            subtitle = "소중한 의견 감사합니다.",
            confirmText = "확인",
            onConfirm = {},
            dismissText = "취소",
            onDismiss = null,
            iconRes = R.drawable.ic_report_check,
            iconVector = null,
        )
    }
}

@Preview(showBackground = true, name = "취소 + 확인 + 아이콘")
@Composable
private fun MoballConfirmDialogPreview() {
    MoballTheme {
        MoballDialogContent(
            title = "펍 즐겨찾기 삭제",
            subtitle = "즐겨찾기 목록에서 삭제할까요?",
            confirmText = "확인",
            onConfirm = {},
            dismissText = "취소",
            onDismiss = {},
            iconRes = R.drawable.ic_trash_full,
            iconVector = null,
        )
    }
}
