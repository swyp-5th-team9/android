package org.app.data.mapper

import org.app.data.model.PubListItem
import org.app.data.model.PubMapItem
import org.app.data.model.PubPage
import org.app.data.model.PubTeam
import org.app.data.remote.dto.GetPubsMapResponse
import org.app.data.remote.dto.GetPubsResponse
import org.app.data.remote.dto.PubDetailResponse
import org.app.data.remote.dto.PubListItemResponse
import org.app.data.remote.dto.PubMapItemResponse
import org.app.data.remote.dto.SupportedTeamResponse
import org.app.presentation.pubdetail.model.BusinessHour
import org.app.presentation.pubdetail.model.KboTeam
import org.app.presentation.pubdetail.model.PubDetail
import org.app.presentation.pubdetail.model.PubMenu
import org.app.presentation.pubdetail.model.PubStatus

// ──────────────────────────────────────────────────────────────
// 펍 목록
// ──────────────────────────────────────────────────────────────

fun GetPubsResponse.toPubPage(): PubPage =
    PubPage(
        content = content.map { it.toPubListItem() },
        page = page,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
    )

fun PubListItemResponse.toPubListItem(): PubListItem =
    PubListItem(
        pubId = pubId,
        name = name,
        region = region,
        address = address,
        thumbnailUrl = thumbnailUrl,
        favoriteCount = favoriteCount,
        businessStatus = businessStatus,
        supportedTeams = supportedTeams.map { it.toPubTeam() },
        facilityCodes = facilityCodes,
        styleCodes = styleCodes,
        themeCodes = themeCodes,
        foodCodes = foodCodes,
    )

fun SupportedTeamResponse.toPubTeam(): PubTeam = PubTeam(teamId = teamId, shortName = shortName, name = name)

// ──────────────────────────────────────────────────────────────
// 펍 상세
// ──────────────────────────────────────────────────────────────

fun PubDetailResponse.toPubDetail(): PubDetail =
    PubDetail(
        pubId = pubId,
        name = name,
        address = address,
        region = region,
        latitude = latitude,
        longitude = longitude,
        phoneNumber = phone,
        status = PubStatus.from(status),
        capacityRange = capacityRange,
        groupSeatMaxPeople = groupSeatMaxPeople,
        favoriteCount = favoriteCount,
        description = description,
        imageUrls = images.sortedBy { it.displayOrder }.map { it.imageUrl },
        teams = supportedTeams.map { KboTeam(it.teamId, it.shortName, it.name) },
        facilityCodes = facilityCodes,
        styleCodes = styleCodes,
        themeCodes = themeCodes,
        foodCodes = foodCodes,
        businessHours = businessHours.sortedBy { it.dayOfWeek }.map {
            BusinessHour(
                dayOfWeek = it.dayOfWeek,
                openTime = it.openTime?.take(5),
                closeTime = it.closeTime?.take(5),
                isClosed = it.isClosed,
            )
        },
        menus = menus.sortedBy { it.displayOrder }.map {
            PubMenu(
                menuId = it.menuId,
                name = it.name,
                category = it.category,
                price = it.price,
                displayOrder = it.displayOrder,
            )
        },
    )

// ──────────────────────────────────────────────────────────────
// 지도 마커
// ──────────────────────────────────────────────────────────────

fun GetPubsMapResponse.toPubMapItems(): List<PubMapItem> = pubs.map { it.toPubMapItem() }

fun PubMapItemResponse.toPubMapItem(): PubMapItem =
    PubMapItem(
        pubId = pubId,
        name = name,
        latitude = latitude,
        longitude = longitude,
        status = status,
        favoriteCount = favoriteCount,
    )
