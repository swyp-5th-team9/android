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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.app.presentation.pubdetail.model.KboTeamType

/** KBO 10개 구단 목록 (전구단 제외, 기존 표시 순서 유지) */
private val KboTeams = listOf(
    KboTeamType.LG,
    KboTeamType.KT,
    KboTeamType.SAMSUNG,
    KboTeamType.HANWHA,
    KboTeamType.KIA,
    KboTeamType.DOOSAN,
    KboTeamType.NC,
    KboTeamType.SSG,
    KboTeamType.LOTTE,
    KboTeamType.KIWOOM,
).map { it.shortName }

/**
 * 응원구단 선택 바텀시트
 *
 * @param initialSelectedTeams 초기 선택된 구단 목록
 * @param onApply               "적용하기" 클릭 시 최종 선택된 구단 목록 전달
 * @param onDismiss             닫기 클릭 (변경 사항 무시)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageTeamSelectBottomSheet(
    initialSelectedTeams: List<String>,
    onApply: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draftSelectedTeams by remember { mutableStateOf(initialSelectedTeams) }

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
            selectedTeams = draftSelectedTeams,
            onTeamClick = { team ->
                if (team in draftSelectedTeams) {
                    draftSelectedTeams = draftSelectedTeams - team
                } else if (draftSelectedTeams.size < 3) {
                    draftSelectedTeams = draftSelectedTeams + team
                }
            },
            onApply = { onApply(draftSelectedTeams) },
            onDismiss = onDismiss,
        )
    }
}

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
            modifier = Modifier.padding(bottom = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(24.dp))

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

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "최대 3팀까지 등록 가능합니다.",
            style = MoballTheme.typography.caption.regular12,
            color = MoballTheme.colors.textTertiary,
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            // 마이페이지에서는 선택 개수와 무관하게 항상 적용 가능 (선택 해제 저장 허용)
            enabled = true,
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

@Preview(showBackground = true)
@Composable
private fun TeamSelectBottomSheetPreview() {
    MoballTheme {
        var selectedTeams by remember { mutableStateOf(listOf("LG", "KT")) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoballTheme.colors.backgroundBase),
        ) {
            TeamSelectBottomSheetContent(
                selectedTeams = selectedTeams,
                onTeamClick = { team ->
                    if (team in selectedTeams) {
                        selectedTeams = selectedTeams - team
                    } else if (selectedTeams.size < 3) {
                        selectedTeams = selectedTeams + team
                    }
                },
                onApply = { },
                onDismiss = { },
            )
        }
    }
}
