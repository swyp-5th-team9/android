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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
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
import org.app.presentation.pubdetail.model.FacilityItem
import org.app.presentation.pubdetail.model.FacilityType
import org.app.presentation.pubdetail.model.ParkingItem
import org.app.presentation.pubdetail.model.ParkingType

@Composable
fun PubFacilitySection(
    facilities: List<FacilityItem>,
    modifier: Modifier = Modifier,
) {
    if (facilities.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "편의시설",
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
                facilities.forEach { item ->
                    FacilityChip(
                        icon = item.type.toIcon(),
                        label = item.type.label,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PubParkingSection(
    parkingItems: List<ParkingItem>,
    modifier: Modifier = Modifier,
) {
    if (parkingItems.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "주차",
            style = MoballTheme.typography.heading6.bold16,
            color = MoballTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(10.dp))
        FacilityCard {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                parkingItems.forEach { item ->
                    FacilityChip(
                        icon = item.type.toIcon(),
                        label = item.type.label,
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

// TODO 아이콘이 추가되기 전까지 기본 내장 아이콘으로 대체 (추후 커스텀 아이콘으로 교체 예정)
private fun FacilityType.toIcon(): ImageVector =
    when (this) {
        FacilityType.GROUP_SEATING -> Icons.Default.Person
        FacilityType.WAITING_AREA -> Icons.Default.Person
        FacilityType.CORKAGE -> Icons.Default.Star
        FacilityType.PRIVATE_ROOM -> Icons.Default.Home
        FacilityType.SMOKING_AREA -> Icons.Default.Warning
        FacilityType.PROJECTOR -> Icons.Default.PlayArrow
        FacilityType.SOUND_SYSTEM -> Icons.Default.Settings
    }

private fun ParkingType.toIcon(): ImageVector =
    when (this) {
        ParkingType.AVAILABLE -> Icons.Default.Place
        ParkingType.NEARBY -> Icons.Default.LocationOn
        ParkingType.VALET -> Icons.Default.Lock
        ParkingType.NONE -> Icons.Default.Info
    }

@Preview(showBackground = true)
@Composable
fun PubFacilitySectionPreview() {
    MoballTheme {
        Column {
            PubFacilitySection(
                facilities = listOf(
                    FacilityItem(FacilityType.GROUP_SEATING),
                    FacilityItem(FacilityType.PROJECTOR),
                    FacilityItem(FacilityType.SOUND_SYSTEM),
                    FacilityItem(FacilityType.PRIVATE_ROOM),
                ),
            )
            PubParkingSection(
                parkingItems = listOf(
                    ParkingItem(ParkingType.AVAILABLE),
                ),
            )
        }
    }
}
