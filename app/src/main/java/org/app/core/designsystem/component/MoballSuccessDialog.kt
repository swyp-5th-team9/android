package org.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.app.core.designsystem.theme.MoballTheme

@Composable
fun MoballSuccessDialog(
    onConfirm: () -> Unit,
    title: String,
    subtitle: String,
) {
    Dialog(
        onDismissRequest = onConfirm,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .width(343.dp)
                .background(
                    color = MoballTheme.colors.backgroundBase,
                    shape = RoundedCornerShape(16.dp),
                ).padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(39.dp))

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        color = MoballTheme.colors.textPrimary,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

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

            MoballButton(
                text = "확인",
                onClick = onConfirm,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoballSuccessDialogPreview() {
    MoballTheme {
        MoballSuccessDialog(
            title = "제보가 접수됐어요",
            subtitle = "소중한 의견 감사합니다.",
            onConfirm = {},
        )
    }
}
