package org.app.presentation.mypage.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

/**
 * 제보 접수 완료 모달 팝업
 *
 * 딤 처리는 [Dialog]의 기본 스크림으로 적용됩니다.
 * "확인" 클릭 시 [onConfirm] 호출 → ReportScreen에서 onBack()으로 마이페이지 복귀.
 */
@Composable
fun ReportSuccessDialog(onConfirm: () -> Unit) {
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
            // Header 영역 (Figma: 24dp padding, empty)
            Spacer(modifier = Modifier.height(39.dp))

            // 확인 아이콘 원형 배지 (88dp, textPrimary 배경)
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

            // "제보가 접수됐어요"
            Text(
                text = "제보가 접수됐어요",
                style = MoballTheme.typography.heading2.bold22,
                color = MoballTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "소중한 의견 감사합니다."
            Text(
                text = "소중한 의견 감사합니다.",
                style = MoballTheme.typography.heading6.semibold16,
                color = MoballTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 확인 버튼 (312dp = 343 - 16*2 - 1 padding)
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoballTheme.colors.accentPrimary,
                    contentColor = MoballTheme.colors.textPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
            ) {
                Text(
                    text = "확인",
                    style = MoballTheme.typography.heading6.bold16,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportSuccessDialogPreview() {
    MoballTheme {
        ReportSuccessDialog(
            onConfirm = {},
        )
    }
}
