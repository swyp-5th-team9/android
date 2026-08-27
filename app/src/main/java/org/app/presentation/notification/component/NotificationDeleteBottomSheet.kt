package org.app.presentation.notification.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.theme.MoballTheme

/**
 * 알림 삭제 확인 바텀시트. 딤(스크림)은 [ModalBottomSheet]가 자동 처리한다.
 * 취소(아웃라인) / 삭제(라임) 버튼은 [MoballButton]을 재사용한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDeleteBottomSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = MoballTheme.colors.staticWhite,
        shape = RectangleShape,
        dragHandle = null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 25.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MoballButton(
                text = "취소",
                onClick = onDismiss,
                modifier = Modifier.width(104.dp),
                backgroundColor = MoballTheme.colors.staticWhite,
                textColor = MoballTheme.colors.textSecondary,
                borderColor = MoballTheme.colors.borderStrong,
            )
            MoballButton(
                text = "삭제",
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
