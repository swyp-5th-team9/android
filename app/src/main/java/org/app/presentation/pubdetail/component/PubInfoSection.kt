package org.app.presentation.pubdetail.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.core.util.TimeUtils
import org.app.data.model.BusinessHour
import org.app.data.model.KboTeam
import org.app.data.model.PubStatus
import org.app.data.model.pubStatusLabel
import org.app.domain.model.KboTeamType
import java.time.LocalTime

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
    styleCodes: List<String>,
    facilityCodes: List<String>,
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
                // 전 구단을 상영하는 펍은 개별 구단 칩을 모두 나열하지 않고 '전구단 상영' 단일 칩으로 표시
                val teamIds = teams.map { it.teamId.toInt() }.toSet()
                val isAllTeams = 0 in teamIds || teamIds.containsAll((1..10).toList())
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (isAllTeams) {
                        TeamBadge(teamType = KboTeamType.ALL)
                    } else {
                        teams.forEach { team ->
                            TeamBadge(teamType = KboTeamType.fromId(team.teamId.toInt()))
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .noRippleClickable(onWishToggle),
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
                text = "약 ${seats}석",
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (styleCodes.isNotEmpty()) {
            FeatureSection(
                title = "경기 상영 스타일",
                features = styleCodes.mapNotNull { mapStyleCodeToFeature(it) },
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (facilityCodes.isNotEmpty()) {
            FeatureSection(
                title = "시설 / 서비스",
                features = facilityCodes.mapNotNull { mapFacilityCodeToFeature(it) },
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureSection(
    title: String,
    features: List<PubFeature>,
) {
    Column {
        Text(
            text = title,
            style = MoballTheme.typography.heading6.bold16,
            color = MoballTheme.colors.textTitle,
        )
        Spacer(modifier = Modifier.height(12.dp))
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MoballTheme.colors.borderNormal, RoundedCornerShape(12.dp))
                .padding(vertical = 24.dp),
        ) {
            val itemWidth = maxWidth / 4
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                maxItemsInEachRow = 4,
            ) {
                features.forEach { feature ->
                    FeatureItem(feature, itemWidth)
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    feature: PubFeature,
    width: androidx.compose.ui.unit.Dp,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(width),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = feature.iconResId),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = feature.label,
            style = MoballTheme.typography.caption.regular12,
            color = MoballTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

private data class PubFeature(
    val label: String,
    val iconResId: Int,
)

private fun mapStyleCodeToFeature(code: String): PubFeature? =
    when (code.lowercase()) {
        "large_screen", "big_screen" -> PubFeature("대형 스크린", R.drawable.ic_pubdetail_screen)
        "single_tv" -> PubFeature("단일 TV", R.drawable.ic_pubdetail_single_tv)
        "multi_tv" -> PubFeature("멀티 TV", R.drawable.ic_pubdetail_multi_tv)
        "broadcast_sound", "sound_system" -> PubFeature(
            "중계 사운드",
            R.drawable.ic_pubdetail_sound,
        )

        else -> null
    }

private fun mapFacilityCodeToFeature(code: String): PubFeature? =
    when (code.lowercase()) {
        "group_seat" -> PubFeature("단체석", R.drawable.ic_pubdetail_people)
        "wide_space", "spacious_view", "spacious_area" -> PubFeature("넓은 공간", R.drawable.ic_pubdetail_space)
        "outdoor_seat" -> PubFeature("야외 좌석", R.drawable.ic_pubdetail_out)
        "parking" -> PubFeature("주차", R.drawable.ic_pubdetail_park)
        "reservation" -> PubFeature("예약 가능", R.drawable.ic_pubdetail_reservation)
        "private_booking" -> PubFeature("대관 가능", R.drawable.ic_pubdetail_booking)
        "counter_seat" -> PubFeature("카운터석", R.drawable.ic_chair)
        "solo_seat" -> PubFeature("1인석", R.drawable.ic_chair)
        "pet_friendly" -> PubFeature("반려동물", R.drawable.ic_pubdetail_people)
        "terrace" -> PubFeature("테라스", R.drawable.ic_pubdetail_out)
        "rooftop" -> PubFeature("루프탑", R.drawable.ic_pubdetail_space)
        else -> null
    }

@Composable
private fun BusinessHoursRow(
    status: PubStatus,
    hours: List<BusinessHour>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    // ISO 기준 오늘 요일 (1=월 … 7=일)
    val now = remember { TimeUtils.nowKst() }
    val todayIso = now.dayOfWeek.value
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
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))

            // 서버 status가 실제 영업시간/휴무를 반영 못 해 businessHours+현재시각 기준으로 보정
            val statusLabel = pubStatusLabel(status, hours, now)
            Text(
                text = statusLabel,
                style = MoballTheme.typography.body.medium14,
                color = MoballTheme.colors.textPrimary,
            )

            if (statusLabel == PubStatus.OPEN.label && todayHours?.closeTime != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${TimeUtils.formatTime(todayHours.closeTime)} 영업 종료",
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
                val isToday = hour.dayOfWeek == todayIso
                Row(
                    modifier = Modifier.padding(start = 26.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val textColor = if (hour.isClosed) {
                        MoballTheme.colors.textTertiary
                    } else if (isToday) {
                        MoballTheme.colors.textPrimary
                    } else {
                        MoballTheme.colors.textSecondary
                    }
                    val textStyle = if (isToday) {
                        MoballTheme.typography.body.medium14
                    } else {
                        MoballTheme.typography.body.regular14
                    }

                    Text(
                        text = hour.dayLabel,
                        style = textStyle,
                        color = textColor,
                        modifier = Modifier.width(24.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when {
                            hour.isClosed -> "휴무"
                            hour.openTime != null && hour.closeTime != null ->
                                "${TimeUtils.formatTime(hour.openTime)} ~ ${TimeUtils.formatTime(hour.closeTime)}"
                            else -> "시간 미정"
                        },
                        style = textStyle,
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
            .then(
                if (onClick != null) {
                    Modifier
                        .noRippleClickable(onClick)
                } else {
                    Modifier
                },
            ),
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
                BusinessHour(1, LocalTime.parse("17:00"), LocalTime.parse("02:00"), false),
                BusinessHour(2, LocalTime.parse("17:00"), LocalTime.parse("02:00"), false),
                BusinessHour(3, LocalTime.parse("17:00"), LocalTime.parse("02:00"), false),
                BusinessHour(4, LocalTime.parse("17:00"), LocalTime.parse("02:00"), false),
                BusinessHour(5, LocalTime.parse("17:00"), LocalTime.parse("03:00"), false),
                BusinessHour(6, LocalTime.parse("15:00"), LocalTime.parse("03:00"), false),
                BusinessHour(7, null, null, true),
            ),
            status = PubStatus.OPEN,
            address = "서울특별시 마포구 월드컵북로 396",
            phoneNumber = "02-1234-5678",
            groupSeatMaxPeople = 30,
            styleCodes = listOf("large_screen", "single_tv", "multi_tv", "broadcast_sound"),
            facilityCodes = listOf(
                "group_seat",
                "wide_space",
                "outdoor_seat",
                "parking",
                "reservation",
                "pet_friendly",
            ),
            isHoursExpanded = false,
            onHoursToggle = {},
            onPhoneCall = {},
            isWished = false,
            favoriteCount = 237,
            onWishToggle = {},
        )
    }
}
