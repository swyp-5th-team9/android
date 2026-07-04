package org.app.data.mapper

import org.app.core.util.TimeUtils
import org.app.data.model.BusinessHour
import org.app.data.model.KboTeam
import org.app.data.model.PubDetail
import org.app.data.model.PubListItem
import org.app.data.model.PubMapItem
import org.app.data.model.PubMenu
import org.app.data.model.PubPage
import org.app.data.model.PubStatus
import org.app.data.model.PubTeam
import org.app.data.remote.dto.GetPubsMapResponse
import org.app.data.remote.dto.GetPubsResponse
import org.app.data.remote.dto.PubDetailResponse
import org.app.data.remote.dto.PubListItemResponse
import org.app.data.remote.dto.PubMapItemResponse
import org.app.data.remote.dto.SupportedTeamResponse

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
                openTime = TimeUtils.parseUtcToKst(it.openTime),
                closeTime = TimeUtils.parseUtcToKst(it.closeTime),
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

fun GetPubsMapResponse.toPubMapItems(): List<PubMapItem> = pubs.map { it.toPubMapItem() }

fun PubMapItem.toPartialDetail(): PubDetail =
    PubDetail(
        pubId = pubId,
        name = name,
        address = "",
        region = "",
        latitude = latitude,
        longitude = longitude,
        phoneNumber = null,
        status = status,
        capacityRange = capacityRange,
        groupSeatMaxPeople = groupSeatMaxPeople,
        favoriteCount = favoriteCount,
        description = null,
        imageUrls = imageUrls,
        teams = emptyList(),
        facilityCodes = facilityCodes,
        styleCodes = emptyList(),
        themeCodes = emptyList(),
        foodCodes = emptyList(),
        businessHours = emptyList(),
        menus = emptyList(),
    )

fun PubMapItem.toPubListItem(): PubListItem =
    PubListItem(
        pubId = pubId,
        name = name,
        region = "",
        address = "",
        thumbnailUrl = imageUrls.firstOrNull(),
        favoriteCount = favoriteCount,
        businessStatus = status.label,
        supportedTeams = supportedTeams.map { PubTeam(0L, it, null) },
        facilityCodes = facilityCodes,
        styleCodes = emptyList(),
        themeCodes = emptyList(),
        foodCodes = emptyList(),
    )

fun PubMapItemResponse.toPubMapItem(): PubMapItem =
    PubMapItem(
        pubId = pubId,
        name = name,
        latitude = latitude,
        longitude = longitude,
        status = PubStatus.from(status),
        favoriteCount = favoriteCount,
        imageUrls = imageUrls ?: emptyList(),
        supportedTeams = supportedTeams ?: emptyList(),
        facilityCodes = facilityCodes ?: emptyList(),
        openTime = TimeUtils.parseUtcToKst(openTime),
        closeTime = TimeUtils.parseUtcToKst(closeTime),
        groupSeatMaxPeople = groupSeatMaxPeople,
        capacityRange = capacityRange,
    )
