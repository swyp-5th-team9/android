package org.app.presentation.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

/** KBO 10개 구단 목록 */
private val KboTeams = listOf("LG", "KT", "삼성", "한화", "KIA", "두산", "NC", "SSG", "롯데", "키움")

/**
 * 응원구단 선택 바텀시트
 *
 * @param selectedTeams 현재 선택된 구단 목록
 * @param onTeamClick   구단 클릭 시 콜백 (선택/해제 토글)
 * @param onApply       "적용하기" 클릭
 * @param onDismiss     닫기 클릭
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSelectBottomSheet(
    selectedTeams: List<String>,
    onTeamClick: (String) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MoballTheme.colors.backgroundBase,
        modifier = modifier,
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
        ),
    ) {
        TeamSelectBottomSheetContent(
            selectedTeams = selectedTeams,
            onTeamClick = onTeamClick,
            onApply = onApply,
            onDismiss = onDismiss,
        )
    }
}

/**
 * 응원구단 선택 바텀시트 내부 콘텐츠
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TeamSelectBottomSheetContent(
    selectedTeams: List<String>,
    onTeamClick: (String) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 17.dp, bottom = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.size(24.dp))

            Text(
                text = "응원구단",
                style = MoballTheme.typography.heading5.bold18,
                color = MoballTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .noRippleClickable(onClick = onDismiss),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "KBO",
            style = MoballTheme.typography.heading5.semibold18,
            color = MoballTheme.colors.textPrimary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(KboTeams) { team ->
                TeamSelectItem(
                    teamName = team,
                    isSelected = team in selectedTeams,
                    onClick = { onTeamClick(team) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        MoballButton(
            text = "적용하기",
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
fun TeamSelectItem(
    teamName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (isSelected) MoballTheme.colors.accentPrimary else MoballTheme.colors.borderStrong
    val bgColor = if (isSelected) MoballTheme.colors.accentTertiaryLight else MoballTheme.colors.backgroundBase

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(bgColor)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = borderColor,
                    shape = CircleShape,
                ).noRippleClickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = teamName,
                style = MoballTheme.typography.heading6.bold16,
                color = MoballTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun TeamSelectBottomSheetPreview() {
    MoballTheme {
        var isBottomSheetShow by remember { mutableStateOf(true) }

        var selectedTeams by remember { mutableStateOf(listOf("LG", "KT")) }

        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Button(
                onClick = { isBottomSheetShow = true },
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "응원구단 선택 열기\n현재 선택: ${selectedTeams.joinToString(", ")}",
                    textAlign = TextAlign.Center,
                )
            }

            if (isBottomSheetShow) {
                TeamSelectBottomSheet(
                    selectedTeams = selectedTeams,
                    onTeamClick = { team ->
                        selectedTeams = if (team in selectedTeams) {
                            selectedTeams - team
                        } else {
                            selectedTeams + team
                        }
                    },
                    onApply = { isBottomSheetShow = false },
                    onDismiss = { isBottomSheetShow = false },
                )
            }
        }
    }
}
