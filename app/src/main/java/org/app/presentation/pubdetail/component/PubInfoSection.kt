package org.app.presentation.pubdetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.pubdetail.model.BusinessHour
import org.app.presentation.pubdetail.model.KboTeam
import org.app.presentation.pubdetail.model.KboTeamType
import org.app.presentation.pubdetail.model.PubStatus
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PubInfoSection(
    pubName: String,
    teams: List<KboTeam>,
    businessHours: List<BusinessHour>,
    address: String,
    phoneNumber: String?,
    groupSeatMaxPeople: Int?,
    status: PubStatus,
    isHoursExpanded: Boolean,
    onHoursToggle: () -> Unit,
    onPhoneCall: () -> Unit,
    isWished: Boolean,
    favoriteCount: Int,
    onWishToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            if (teams.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    teams.forEach { team ->
                        TeamBadge(teamType = KboTeamType.fromId(team.teamId.toInt()))
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.noRippleClickable(onWishToggle),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = if (isWished) R.drawable.ic_heart_fill else R.drawable.ic_heart,
                    ),
                    contentDescription = null,
                    tint = if (isWished) MoballTheme.colors.iconPrimary else MoballTheme.colors.iconSecondary,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = favoriteCount.toString(),
                    style = MoballTheme.typography.caption.regular12,
                    color = MoballTheme.colors.textTertiary,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = pubName,
            style = MoballTheme.typography.heading1.bold24,
            color = MoballTheme.colors.textTitle,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (businessHours.isNotEmpty()) {
            BusinessHoursRow(
                status = status,
                hours = businessHours,
                isExpanded = isHoursExpanded,
                onToggle = onHoursToggle,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        InfoRow(
            icon = ImageVector.vectorResource(R.drawable.ic_pubdetail_location),
            text = address,
        )

        phoneNumber?.let { phone ->
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_phone),
                text = phone,
                onClick = onPhoneCall,
            )
        }

        groupSeatMaxPeople?.let { seats ->
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_chair),
                text = "단체석 최대 ${seats}명",
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun BusinessHoursRow(
    status: PubStatus,
    hours: List<BusinessHour>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    // ISO 기준 오늘 요일 (1=월 … 7=일)
    val todayIso = remember { LocalDate.now().dayOfWeek.value }
    val todayHours = hours.firstOrNull { it.dayOfWeek == todayIso }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable(onToggle),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_clock),
                contentDescription = null,
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = status.label,
                style = MoballTheme.typography.body.medium14,
                color = when (status) {
                    PubStatus.OPEN -> MoballTheme.colors.accentPrimary
                    PubStatus.CLOSED -> MoballTheme.colors.textTertiary
                    PubStatus.TEMP_CLOSED -> MoballTheme.colors.textSecondary
                },
            )

            if (todayHours != null && !todayHours.isClosed) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${todayHours.openTime} - ${todayHours.closeTime}",
                    style = MoballTheme.typography.body.regular14,
                    color = MoballTheme.colors.textSecondary,
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MoballTheme.colors.iconTertiary,
                modifier = Modifier.size(20.dp),
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(6.dp))
            hours.sortedBy { it.dayOfWeek }.forEach { hour ->
                Row(
                    modifier = Modifier.padding(start = 26.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = hour.dayLabel,
                        style = MoballTheme.typography.caption.regular12,
                        color = MoballTheme.colors.textTertiary,
                        modifier = Modifier.width(24.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    val textColor =
                        if (hour.isClosed) {
                            MoballTheme.colors.textTertiary
                        } else {
                            MoballTheme.colors.textSecondary
                        }
                    Text(
                        text = if (hour.isClosed) "휴무" else "${hour.openTime} - ${hour.closeTime}",
                        style = MoballTheme.typography.caption.regular12,
                        color = textColor,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.noRippleClickable(onClick) else Modifier),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MoballTheme.typography.body.regular14,
            color = MoballTheme.colors.textPrimary,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PubInfoSectionPreview() {
    MoballTheme {
        PubInfoSection(
            pubName = "시그니처 펍",
            teams = listOf(
                KboTeam(teamId = 3L, shortName = "LG", name = "LG 트윈스"),
                KboTeam(teamId = 6L, shortName = "두산", name = "두산 베어스"),
            ),
            businessHours = listOf(
                BusinessHour(1, "17:00", "02:00", false),
                BusinessHour(2, "17:00", "02:00", false),
                BusinessHour(3, "17:00", "02:00", false),
                BusinessHour(4, "17:00", "02:00", false),
                BusinessHour(5, "17:00", "03:00", false),
                BusinessHour(6, "15:00", "03:00", false),
                BusinessHour(7, null, null, true),
            ),
            status = PubStatus.OPEN,
            address = "서울특별시 마포구 월드컵북로 396",
            phoneNumber = "02-1234-5678",
            groupSeatMaxPeople = 30,
            isHoursExpanded = false,
            onHoursToggle = {},
            onPhoneCall = {},
            isWished = false,
            favoriteCount = 237,
            onWishToggle = {},
        )
    }
}
