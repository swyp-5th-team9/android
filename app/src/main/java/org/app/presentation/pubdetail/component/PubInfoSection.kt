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
import org.app.presentation.pubdetail.model.BusinessHours
import org.app.presentation.pubdetail.model.BusinessStatus
import org.app.presentation.pubdetail.model.KboTeam
import org.app.presentation.pubdetail.model.KboTeamType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PubInfoSection(
    pubName: String,
    teams: List<KboTeam>,
    businessHours: BusinessHours?,
    address: String,
    phoneNumber: String?,
    seatCount: Int?,
    isHoursExpanded: Boolean,
    onHoursToggle: () -> Unit,
    onPhoneCall: () -> Unit,
    isWished: Boolean,
    wishCount: Int,
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
                        TeamBadge(teamType = KboTeamType.fromId(team.id))
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
                    text = wishCount.toString(),
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

        businessHours?.let { hours ->
            BusinessHoursRow(
                hours = hours,
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

        seatCount?.let { seats ->
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_chair),
                text = "좌석 ${seats}석",
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun BusinessHoursRow(
    hours: BusinessHours,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
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

            val statusText = when (hours.status) {
                BusinessStatus.OPEN -> "영업중"
                BusinessStatus.CLOSED -> "영업종료"
                BusinessStatus.BREAK -> "브레이크타임"
            }

            Text(
                text = statusText,
                style = MoballTheme.typography.body.medium14,
                color = MoballTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${hours.openTime} - ${hours.closeTime}",
                style = MoballTheme.typography.body.regular14,
                color = MoballTheme.colors.textSecondary,
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MoballTheme.colors.iconTertiary,
                modifier = Modifier.size(20.dp),
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(6.dp))
            hours.lastOrder?.let { lo ->
                Row(modifier = Modifier.padding(start = 26.dp)) {
                    Text(
                        text = "라스트오더",
                        style = MoballTheme.typography.caption.regular12,
                        color = MoballTheme.colors.textTertiary,
                        modifier = Modifier.width(80.dp),
                    )
                    Text(
                        text = lo,
                        style = MoballTheme.typography.caption.regular12,
                        color = MoballTheme.colors.textSecondary,
                    )
                }
            }
            hours.breakStartTime?.let { bs ->
                Row(modifier = Modifier.padding(start = 26.dp, top = 4.dp)) {
                    Text(
                        text = "브레이크타임",
                        style = MoballTheme.typography.caption.regular12,
                        color = MoballTheme.colors.textTertiary,
                        modifier = Modifier.width(80.dp),
                    )
                    Text(
                        text = hours.breakEndTime?.let { be -> "$bs - $be" } ?: bs,
                        style = MoballTheme.typography.caption.regular12,
                        color = MoballTheme.colors.textSecondary,
                    )
                }
            }
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
            modifier = Modifier
                .size(18.dp),
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
                KboTeam(id = 3, shortName = "LG", fullName = "LG 트윈스"),
                KboTeam(id = 6, shortName = "두산", fullName = "두산 베어스"),
            ),
            businessHours = BusinessHours(
                openTime = "17:00",
                closeTime = "02:00",
                lastOrder = "01:30",
                isOpenNow = true,
                status = BusinessStatus.OPEN,
            ),
            address = "서울특별시 마포구 월드컵북로 396",
            phoneNumber = "02-1234-5678",
            seatCount = 60,
            isHoursExpanded = false,
            onHoursToggle = {},
            onPhoneCall = {},
            isWished = false,
            wishCount = 237,
            onWishToggle = {},
        )
    }
}
