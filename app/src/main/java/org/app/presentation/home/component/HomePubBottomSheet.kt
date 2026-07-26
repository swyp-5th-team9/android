package org.app.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import org.app.core.designsystem.component.UrlImage
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.core.util.TimeUtils
import org.app.data.model.PubDetail
import org.app.data.model.PubMapItem
import org.app.data.model.PubStatus
import org.app.data.model.PubTeam
import org.app.data.model.pubStatusLabel
import org.app.domain.model.KboTeamType
import org.app.presentation.home.model.HomeFilter
import org.app.presentation.home.pubfilter.FacilityCode
import org.app.presentation.home.pubfilter.FoodCode
import org.app.presentation.pubdetail.component.TeamBadge
import org.app.presentation.pubdetail.component.TeamListBadge
import java.time.LocalDate
import java.time.LocalTime

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 8.dp)
            .width(40.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(MoballTheme.colors.borderNormal),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePubListBottomSheet(
    pubItems: List<PubMapItem>,
    favoritePubIds: Set<Long>,
    filter: HomeFilter,
    pubDetails: Map<Long, PubDetail>,
    onItemAppear: (Long) -> Unit,
    onItemClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onFilterClick: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        contentWindowInsets = { WindowInsets(0) },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = { DragHandle() },
    ) {
        PubListContent(
            pubItems = pubItems,
            favoritePubIds = favoritePubIds,
            filter = filter,
            pubDetails = pubDetails,
            onItemAppear = onItemAppear,
            onItemClick = onItemClick,
            onFavoriteClick = onFavoriteClick,
            onFilterClick = onFilterClick,
        )
    }
}

@Composable
private fun PubListContent(
    pubItems: List<PubMapItem>,
    favoritePubIds: Set<Long>,
    filter: HomeFilter,
    pubDetails: Map<Long, PubDetail>,
    onItemAppear: (Long) -> Unit,
    onItemClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onFilterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 펍 개수가 많을 때 리스트가 화면을 넘어 잘리지 않도록,
    // 시트 최대 높이를 화면의 90%로 제한하고 리스트 영역만 스크롤한다.
    val maxSheetHeight = (LocalConfiguration.current.screenHeightDp * 0.9f).dp
    Column(
        modifier = modifier
            .heightIn(max = maxSheetHeight)
            // 시트가 시스템 내비게이션 바(하단바)를 침범하지 않도록 하단 인셋 확보
            .navigationBarsPadding(),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                HomePubFilterChip(
                    label = "영업중",
                    isSelected = filter.openNow == true,
                    onClick = { onFilterClick("OPEN") },
                )
            }
            item {
                HomePubFilterChip(
                    label = "단체석",
                    isSelected = filter.facilityCodes?.contains(FacilityCode.GROUP_SEAT.code) == true,
                    onClick = { onFilterClick("GROUP_SEAT") },
                )
            }
            item {
                HomePubFilterChip(
                    label = "주차",
                    isSelected = filter.facilityCodes?.contains(FacilityCode.PARKING.code) == true,
                    onClick = { onFilterClick("PARKING") },
                )
            }
            item {
                HomePubFilterChip(
                    label = "넓은",
                    isSelected = filter.facilityCodes?.contains(FacilityCode.SPACIOUS_AREA.code) == true,
                    onClick = { onFilterClick("WIDE_SPACE") },
                )
            }
            item {
                HomePubFilterChip(
                    label = "다양한 술",
                    isSelected = filter.foodCodes?.any { it in FoodCode.DRINK_CODES } == true,
                    onClick = { onFilterClick("VARIOUS_DRINKS") },
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(pubItems, key = { it.pubId }) { item ->
                PubListItem(
                    item = item,
                    detail = pubDetails[item.pubId],
                    isFavorite = item.pubId in favoritePubIds,
                    onAppear = { onItemAppear(item.pubId) },
                    onClick = { onItemClick(item.pubId) },
                    onFavoriteClick = { onFavoriteClick(item.pubId) },
                )
            }
        }
    }
}

@Composable
private fun PubListItem(
    item: PubMapItem,
    detail: PubDetail?,
    isFavorite: Boolean,
    onAppear: () -> Unit,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    LaunchedEffect(item.pubId) { onAppear() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.name,
                style = MoballTheme.typography.heading6.bold16,
                color = MoballTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                imageVector = ImageVector.vectorResource(
                    if (isFavorite) R.drawable.ic_heart_fill else R.drawable.ic_heart,
                ),
                contentDescription = null,
                tint = if (isFavorite) MoballTheme.colors.iconPrimary else MoballTheme.colors.textTertiary,
                modifier = Modifier
                    .size(24.dp)
                    .noRippleClickable(onFavoriteClick),
            )
        }
        Spacer(Modifier.height(12.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val itemSize = (maxWidth - 24.dp) / 4
            val imagesToShow = item.imageUrls.take(4).ifEmpty { listOf(null) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                imagesToShow.forEachIndexed { index, url ->
                    UrlImage(
                        url = url,
                        modifier = Modifier
                            .width(itemSize)
                            // 화면 폭에 따라 셀 너비가 변해도 썸네일 비율(89:92) 유지
                            .aspectRatio(89f / 92f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MoballTheme.colors.borderNormal),
                        contentScale = ContentScale.Crop,
                        placeholderRes = R.drawable.img_moball_empty,
                    )
                    if (index < imagesToShow.size - 1) {
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MoballTheme.colors.borderNormal, RoundedCornerShape(8.dp))
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = MoballTheme.colors.textPrimary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(4.dp))
                // 상세가 로드됐으면 businessHours 기준(휴무/영업시간까지 정확), 아직이면 지도 status로 임시 표시
                val now = remember { TimeUtils.nowKst() }
                val todayHours = detail?.businessHours?.firstOrNull { it.dayOfWeek == now.dayOfWeek.value }
                val statusLabel = if (detail != null) {
                    pubStatusLabel(detail.status, detail.businessHours, now)
                } else {
                    pubStatusLabel(item.status, item.openTime, item.closeTime, now)
                }
                val timeRange = when {
                    todayHours?.isClosed == true -> ""
                    todayHours?.openTime != null && todayHours.closeTime != null ->
                        " ${TimeUtils.formatTime(todayHours.openTime)} - ${TimeUtils.formatTime(todayHours.closeTime)}"
                    item.openTime != null && item.closeTime != null ->
                        " ${TimeUtils.formatTime(item.openTime)} - ${TimeUtils.formatTime(item.closeTime)}"
                    else -> ""
                }
                Text(
                    text = "$statusLabel$timeRange",
                    style = MoballTheme.typography.heading6.semibold16,
                    color = MoballTheme.colors.textPrimary,
                )
            }
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                if (item.supportedTeams.isNotEmpty()) {
                    // 전 구단 지원이면 첫 팀명 대신 '전구단' (상세 화면과 동일)
                    val teamLabel = if (isAllTeams(item.supportedTeams.map { it.teamId })) {
                        KboTeamType.ALL.shortName
                    } else {
                        item.supportedTeams.first().shortName
                    }
                    TeamListBadge(text = teamLabel)
                }
                item.facilityCodes.firstOrNull()?.let { code ->
                    mapFacilityCodeToLabel(code)?.let { label ->
                        TeamListBadge(text = label)
                    }
                }
                item.groupSeatMaxPeople?.let { count ->
                    TeamListBadge(text = "${count}명 수용가능")
                }
            }
        }
    }
}

/** 지원 구단이 전 구단(teamId 0 또는 1~10 전체)인지 — 펍 상세 화면(PubInfoSection)과 동일 판정 */
private fun isAllTeams(teamIds: List<Long>): Boolean {
    val ids = teamIds.map { it.toInt() }.toSet()
    return 0 in ids || ids.containsAll((1..10).toList())
}

private fun mapFacilityCodeToLabel(code: String): String? =
    when (code.lowercase()) {
        "group_seat" -> "단체석"
        "wide_space", "spacious_view" -> "넓은 공간"
        "outdoor_seat" -> "야외 좌석"
        "parking" -> "주차"
        "reservation" -> "예약가능"
        else -> null
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePubDetailBottomSheet(
    detail: PubDetail?,
    isLoading: Boolean,
    isFavoriteLoading: Boolean,
    onFavoriteClick: () -> Unit,
    onKakaoMapClick: () -> Unit,
    onNaverMapClick: () -> Unit,
    onCardClick: (Long) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        contentWindowInsets = { WindowInsets(0) },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = { DragHandle() },
    ) {
        PubDetailContent(
            detail = detail,
            isLoading = isLoading,
            isFavoriteLoading = isFavoriteLoading,
            onFavoriteClick = onFavoriteClick,
            onKakaoMapClick = onKakaoMapClick,
            onNaverMapClick = onNaverMapClick,
            onCardClick = onCardClick,
        )
    }
}

@Composable
private fun PubDetailContent(
    detail: PubDetail?,
    isLoading: Boolean,
    isFavoriteLoading: Boolean,
    onFavoriteClick: () -> Unit,
    onKakaoMapClick: () -> Unit,
    onNaverMapClick: () -> Unit,
    onCardClick: (Long) -> Unit,
) {
    // 시트 하단 버튼(찜/길찾기)이 시스템 내비게이션 바(하단바)와 겹치지 않도록 하단 인셋 확보
    Column(modifier = Modifier.navigationBarsPadding()) {
        if (detail == null && isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MoballTheme.colors.accentPrimary)
            }
        } else if (detail != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { onCardClick(detail.pubId) }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (detail.teams.isNotEmpty()) {
                        // 전 구단 지원이면 개별 팀 대신 '전구단 상영' 배지 (상세 화면과 동일)
                        val teamType = if (isAllTeams(detail.teams.map { it.teamId })) {
                            KboTeamType.ALL
                        } else {
                            KboTeamType.fromId(
                                detail.teams
                                    .first()
                                    .teamId
                                    .toInt(),
                            )
                        }
                        TeamBadge(teamType = teamType)
                    }

                    Text(
                        text = detail.name,
                        style = MoballTheme.typography.heading3.bold20,
                        color = MoballTheme.colors.textPrimary,
                    )

                    if (detail.address.isNotEmpty()) {
                        Text(
                            text = detail.address.summaryAddress(),
                            style = MoballTheme.typography.body.regular14,
                            color = MoballTheme.colors.textSecondary,
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val now = remember { TimeUtils.nowKst() }
                        val todayHours = detail.businessHours.find { it.dayOfWeek == now.dayOfWeek.value }
                        val isClosedToday = todayHours?.isClosed == true

                        // 서버 status가 실제 영업시간/휴무를 반영 못 해 businessHours+현재시각 기준으로 보정
                        Text(
                            text = pubStatusLabel(detail.status, detail.businessHours, now),
                            style = MoballTheme.typography.body.medium14,
                            color = MoballTheme.colors.textPrimary,
                        )

                        // 휴무일이면 라벨이 이미 "휴무"이므로 영업시간은 중복 표시하지 않는다
                        if (!isClosedToday && todayHours?.openTime != null && todayHours.closeTime != null) {
                            Spacer(Modifier.width(6.dp))
                            val open = TimeUtils.formatTime(todayHours.openTime)
                            val close = TimeUtils.formatTime(todayHours.closeTime)
                            Text(
                                text = "$open - $close",
                                style = MoballTheme.typography.body.regular14,
                                color = MoballTheme.colors.textSecondary,
                            )
                        } else if (detail.businessHours.isEmpty() && isLoading) {
                            Spacer(Modifier.width(6.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp,
                                color = MoballTheme.colors.accentPrimary,
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        detail.facilityCodes.take(2).forEach { code ->
                            mapFacilityCodeToLabel(code)?.let { label ->
                                TeamListBadge(text = label)
                            }
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                UrlImage(
                    url = detail.imageUrls.firstOrNull(),
                    contentDescription = detail.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MoballTheme.colors.borderNormal),
                    placeholderRes = R.drawable.img_moball_empty,
                )
            }
        }

        HorizontalDivider(color = MoballTheme.colors.borderNormal)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, MoballTheme.colors.borderNormal, RoundedCornerShape(12.dp))
                    .then(
                        if (!isFavoriteLoading) {
                            Modifier.noRippleClickable(onFavoriteClick)
                        } else {
                            Modifier
                        },
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (detail?.isFavoriteed == true) {
                            R.drawable.ic_heart_fill
                        } else {
                            R.drawable.ic_heart
                        },
                    ),
                    contentDescription = "즐겨찾기",
                    tint = if (detail?.isFavoriteed == true) {
                        MoballTheme.colors.iconPrimary
                    } else {
                        MoballTheme.colors.textSecondary
                    },
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = (detail?.favoriteCount ?: 0).toString(),
                    style = MoballTheme.typography.caption.regular12,
                    color = MoballTheme.colors.textSecondary,
                )
            }

            MapActionButton(
                text = "카카오맵",
                onClick = onKakaoMapClick,
                modifier = Modifier.weight(1f),
            )
            MapActionButton(
                text = "네이버지도",
                onClick = onNaverMapClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun MapActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .heightIn(min = 56.dp)
            .background(MoballTheme.colors.backgroundScrim)
            .noRippleClickable(onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MoballTheme.typography.heading6.bold16,
            color = MoballTheme.colors.staticWhite,
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_up_right_md),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp),
        )
    }
}

private fun String.summaryAddress(): String {
    val parts = trim().split(" ")
    // "서울시 강남구 청담동" 처럼 3단위까지만 추출
    return parts.take(2).joinToString(" ")
}

@Composable
private fun HomePubFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                color = if (isSelected) MoballTheme.colors.borderActive else MoballTheme.colors.backgroundBase,
            ).border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else MoballTheme.colors.borderStrong,
                shape = CircleShape,
            ).noRippleClickable(onClick)
            .heightIn(min = 32.dp)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MoballTheme.typography.heading7.semibold14,
            color = if (isSelected) MoballTheme.colors.textPrimary else MoballTheme.colors.textTertiary,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun HomePubListBottomSheetPreview() {
    val samplePubs = listOf(
        PubMapItem(
            pubId = 1L,
            name = "모볼 펍 강남점",
            latitude = 37.0,
            longitude = 127.0,
            status = PubStatus.OPEN,
            favoriteCount = 128,
            imageUrls = listOf("https://sample.com/1.jpg"),
            supportedTeams = listOf(PubTeam(9L, "한화", null)),
            facilityCodes = listOf("group_seat"),
            openTime = LocalTime.of(8, 0),
            closeTime = LocalTime.of(0, 0),
            groupSeatMaxPeople = 100,
            capacityRange = "20-30",
        ),
    )

    MoballTheme {
        PubListContent(
            pubItems = samplePubs,
            favoritePubIds = setOf(1L),
            filter = org.app.presentation.home.model
                .HomeFilter(),
            pubDetails = emptyMap(),
            onItemAppear = {},
            onItemClick = {},
            onFavoriteClick = {},
            onFilterClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun HomePubDetailBottomSheetPreview() {
    val sampleDetail = PubDetail(
        pubId = 1L,
        name = "시그니처 펍",
        address = "서울시 강남구 청담동 123-45",
        region = "강남구",
        latitude = 37.0,
        longitude = 127.0,
        phoneNumber = "02-123-4567",
        status = PubStatus.OPEN,
        capacityRange = "20-30",
        groupSeatMaxPeople = 10,
        favoriteCount = 237,
        description = "야구와 함께하는 즐거운 시간",
        imageUrls = listOf(""),
        teams = listOf(
            org.app.data.model
                .KboTeam(9L, "롯데", "롯데 자이언츠"),
        ),
        facilityCodes = listOf("parking", "group_seat"),
        styleCodes = emptyList(),
        themeCodes = emptyList(),
        foodCodes = emptyList(),
        businessHours = listOf(
            org.app.data.model.BusinessHour(
                LocalDate.now().dayOfWeek.value,
                LocalTime.of(8, 0),
                LocalTime.of(0, 0),
                false,
            ),
        ),
        menus = emptyList(),
        isFavoriteed = false,
    )

    MoballTheme {
        PubDetailContent(
            detail = sampleDetail,
            isLoading = false,
            isFavoriteLoading = false,
            onFavoriteClick = {},
            onKakaoMapClick = {},
            onNaverMapClick = {},
            onCardClick = {},
        )
    }
}
