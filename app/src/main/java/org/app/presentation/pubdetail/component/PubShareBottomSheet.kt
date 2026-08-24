package org.app.presentation.pubdetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moball.app.R
import kotlinx.coroutines.delay
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

private const val COPIED_RESET_MS = 3000L

/**
 * 펍 공유 바텀시트. `링크 복사` / `다른 앱에 공유` 두 옵션을 제공한다.
 *
 * - 링크 복사: 클립보드 복사([onCopyLink]) 후 3초간 "복사됨" 상태를 노출하고 원복(디자인).
 * - 다른 앱에 공유: 안드로이드 기본 공유 시트([onShareOther]).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PubShareBottomSheet(
    onCopyLink: () -> Unit,
    onShareOther: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(COPIED_RESET_MS)
            copied = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = MoballTheme.colors.staticWhite,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
        ) {
            ShareOptionRow(
                icon = ImageVector.vectorResource(R.drawable.ic_copy),
                label = if (copied) "링크가 복사되었어요" else "링크 복사",
                onClick = {
                    onCopyLink()
                    copied = true
                },
            )
            ShareOptionRow(
                icon = ImageVector.vectorResource(R.drawable.ic_share),
                label = "다른 앱에 공유",
                onClick = onShareOther,
            )
        }
    }
}

@Composable
private fun ShareOptionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MoballTheme.colors.iconPrimary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            style = MoballTheme.typography.body.medium16,
            color = MoballTheme.colors.textPrimary,
        )
    }
}
