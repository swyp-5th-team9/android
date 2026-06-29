package org.app.presentation.pubdetail.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme

/**
 * 편의시설 / 스타일 / 테마 / 음식 코드 문자열 목록을 아이콘 칩으로 표시.
 * 빈 목록이면 아무것도 렌더링하지 않는다.
 */
@Composable
fun PubFacilitySection(
    title: String = "편의시설",
    codes: List<String>,
    modifier: Modifier = Modifier,
) {
    if (codes.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = title,
            style = MoballTheme.typography.heading6.bold16,
            color = MoballTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        FacilityCard {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                codes.forEach { code ->
                    FacilityChip(
                        icon = codeToIcon(code),
                        label = codeToLabel(code),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FacilityCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MoballTheme.colors.borderNormal,
                shape = RoundedCornerShape(12.dp),
            ),
        shape = RoundedCornerShape(12.dp),
        color = MoballTheme.colors.backgroundBase,
    ) {
        content()
    }
}

@Composable
private fun FacilityChip(
    icon: ImageVector,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MoballTheme.colors.iconSecondary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            style = MoballTheme.typography.caption.regular12,
            color = MoballTheme.colors.textSecondary,
        )
    }
}

// TODO 커스텀 아이콘 디자인 에셋이 추가되면 교체
private fun codeToIcon(code: String): ImageVector =
    when (code) {
        "GROUP_SEAT" -> Icons.Default.Person
        "SOLO_SEAT" -> Icons.Default.Person
        "PARKING" -> Icons.Default.Info
        "MULTI_TV", "BIG_SCREEN" -> Icons.Default.PlayArrow
        "PRIVATE_ROOM" -> Icons.Default.Home
        "SMOKING_AREA" -> Icons.Default.Warning
        "SOUND_SYSTEM" -> Icons.Default.Settings
        "OFFICIAL_PUB" -> Icons.Default.Star
        "STADIUM_MODE" -> Icons.Default.PlayArrow
        "CHICKEN" -> Icons.Default.Star
        "BEER", "SOJU" -> Icons.Default.Info
        "PIZZA" -> Icons.Default.Home
        else -> Icons.Default.Info
    }

private fun codeToLabel(code: String): String =
    when (code) {
        "GROUP_SEAT" -> "단체석"
        "SOLO_SEAT" -> "혼자석"
        "PARKING" -> "주차"
        "MULTI_TV" -> "다중 TV"
        "BIG_SCREEN" -> "대형 스크린"
        "PRIVATE_ROOM" -> "단독룸"
        "SMOKING_AREA" -> "흡연 구역"
        "SOUND_SYSTEM" -> "음향 시스템"
        "OFFICIAL_PUB" -> "공식 펍"
        "STADIUM_MODE" -> "경기장 모드"
        "SPACIOUS_VIEW" -> "탁 트인 뷰"
        "COMFY_SEAT" -> "편안한 좌석"
        "CHICKEN" -> "치킨"
        "BEER" -> "맥주"
        "SOJU" -> "소주"
        "PIZZA" -> "피자"
        "SET" -> "세트 메뉴"
        else -> code
    }

@Preview(showBackground = true)
@Composable
fun PubFacilitySectionPreview() {
    MoballTheme {
        Column {
            PubFacilitySection(
                codes = listOf("GROUP_SEAT", "BIG_SCREEN", "OFFICIAL_PUB", "PARKING"),
            )
        }
    }
}
