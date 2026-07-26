package org.app.presentation.home.pubfilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.component.LocalMoballToastHostState
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.home.pubfilter.CITIES
import org.app.presentation.home.pubfilter.SEOUL_SUB_REGIONS

/** 서울 외 지역(경기·인천·부산 등) 선택 시 안내 문구 — MVP는 서울만 지원. */
const val UNSUPPORTED_REGION_MESSAGE = "현재는 서울 지역만 지원하고 있어요"

@Composable
fun PubFilterRegionSectionContent(
    selectedSubRegionIds: Set<String>,
    onToggleSubRegion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "지역",
            style = MoballTheme.typography.heading3.bold20,
            color = MoballTheme.colors.textPrimary,
        )

        Spacer(Modifier.height(24.dp))

        val toastHostState = LocalMoballToastHostState.current
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CITIES.forEach { city ->
                CityChip(
                    label = city.label,
                    isSelected = city.id == "seoul",
                    isEnabled = city.isEnabled,
                    onClick = {
                        // MVP: 서울만 지원. 그 외 지역은 안내 토스트만 표시.
                        if (!city.isEnabled) {
                            toastHostState.show(UNSUPPORTED_REGION_MESSAGE)
                        }
                    },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MoballTheme.colors.backgroundSurface)
                .padding(16.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SEOUL_SUB_REGIONS.forEach { option ->
                    PubFilterSubRegionChip(
                        label = option.label,
                        isSelected = option.id in selectedSubRegionIds,
                        onClick = { onToggleSubRegion(option.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun CityChip(
    label: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (isEnabled && isSelected) {
        MoballTheme.colors.accentPrimary
    } else {
        MoballTheme.colors.backgroundBase
    }
    val textColor = when {
        !isEnabled -> MoballTheme.colors.textTertiary
        isSelected -> MoballTheme.colors.textPrimary
        else -> MoballTheme.colors.textPrimary
    }
    val borderColor = if (isEnabled && isSelected) {
        MoballTheme.colors.accentPrimary
    } else {
        MoballTheme.colors.borderNormal
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            // 비활성 도시도 클릭은 가능하게 두어 호출부에서 안내 토스트를 띄운다(시각 스타일만 비활성).
            .noRippleClickable(onClick)
            .heightIn(min = 44.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MoballTheme.typography.body.medium14,
            color = textColor,
            // 칩 라벨이 좁은 화면에서 줄바꿈되어 모양이 깨지지 않도록 한 줄 고정
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}
