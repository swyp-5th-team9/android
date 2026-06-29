package org.app.data.remote.datasource.impl

import com.moball.app.BuildConfig
import org.app.data.remote.datasource.api.PubRemoteDataSource
import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.BusinessHourResponse
import org.app.data.remote.dto.GetPubsMapResponse
import org.app.data.remote.dto.GetPubsResponse
import org.app.data.remote.dto.MenuResponse
import org.app.data.remote.dto.PubDetailResponse
import org.app.data.remote.dto.PubImageResponse
import org.app.data.remote.dto.PubListItemResponse
import org.app.data.remote.dto.PubMapItemResponse
import org.app.data.remote.dto.SupportedTeamResponse
import org.app.data.remote.service.PubService
import javax.inject.Inject

class PubRemoteDataSourceImpl
    @Inject
    constructor(
        private val pubService: PubService,
    ) : PubRemoteDataSource {
        // ──────────────────────────────────────────────────────────────
        // Mock 데이터
        // ──────────────────────────────────────────────────────────────

        private val mockTeams = listOf(
            SupportedTeamResponse(1L, "LG", "LG 트윈스"),
            SupportedTeamResponse(2L, "두산", "두산 베어스"),
            SupportedTeamResponse(3L, "KT", "KT 위즈"),
        )

        private val mockListItems = listOf(
            PubListItemResponse(
                pubId = 1L,
                name = "시그니처 펍",
                region = "MAPO",
                address = "서울 마포구 홍대로 10",
                thumbnailUrl = null,
                favoriteCount = 24,
                businessStatus = "OPEN_NOW",
                supportedTeams = listOf(mockTeams[0], mockTeams[1]),
                facilityCodes = listOf("GROUP_SEAT", "PARKING"),
                styleCodes = listOf("BIG_SCREEN", "OFFICIAL_PUB"),
                themeCodes = listOf("SPACIOUS_VIEW"),
                foodCodes = listOf("CHICKEN", "BEER"),
            ),
            PubListItemResponse(
                pubId = 2L,
                name = "야구 천국",
                region = "SONGPA",
                address = "서울 송파구 올림픽로 20",
                thumbnailUrl = null,
                favoriteCount = 18,
                businessStatus = "OPEN_NOW",
                supportedTeams = listOf(mockTeams[0]),
                facilityCodes = listOf("GROUP_SEAT", "MULTI_TV"),
                styleCodes = listOf("STADIUM_MODE"),
                themeCodes = emptyList(),
                foodCodes = listOf("CHICKEN", "BEER", "SOJU"),
            ),
            PubListItemResponse(
                pubId = 3L,
                name = "응원 펍 잠실",
                region = "SONGPA",
                address = "서울 송파구 잠실동 100",
                thumbnailUrl = null,
                favoriteCount = 9,
                businessStatus = "CLOSED_NOW",
                supportedTeams = listOf(mockTeams[1]),
                facilityCodes = listOf("SOLO_SEAT"),
                styleCodes = listOf("MULTI_TV"),
                themeCodes = listOf("COMFY_SEAT"),
                foodCodes = listOf("PIZZA", "BEER"),
            ),
        )

        private val mockDetail = PubDetailResponse(
            pubId = 1L,
            name = "시그니처 펍",
            address = "서울 마포구 홍대로 10",
            region = "MAPO",
            latitude = 37.5516,
            longitude = 126.9086,
            phone = "02-1234-5678",
            status = "OPEN",
            capacityRange = "R_50_100",
            groupSeatMaxPeople = 30,
            favoriteCount = 24,
            description = "LG 트윈스 공식 응원 펍. 대형 스크린 3개 보유. 단체 예약 가능.",
            images = listOf(
                PubImageResponse(1L, "https://picsum.photos/seed/pub1/400/400", 0),
                PubImageResponse(2L, "https://picsum.photos/seed/pub2/400/400", 1),
                PubImageResponse(3L, "https://picsum.photos/seed/pub3/400/400", 2),
            ),
            supportedTeams = listOf(mockTeams[0], mockTeams[1]),
            facilityCodes = listOf("GROUP_SEAT", "PARKING"),
            styleCodes = listOf("BIG_SCREEN", "OFFICIAL_PUB"),
            themeCodes = listOf("SPACIOUS_VIEW"),
            foodCodes = listOf("CHICKEN", "BEER"),
            businessHours = listOf(
                BusinessHourResponse(1, "17:00", "02:00", false),
                BusinessHourResponse(2, "17:00", "02:00", false),
                BusinessHourResponse(3, "17:00", "02:00", false),
                BusinessHourResponse(4, "17:00", "02:00", false),
                BusinessHourResponse(5, "17:00", "03:00", false),
                BusinessHourResponse(6, "15:00", "03:00", false),
                BusinessHourResponse(7, null, null, true),
            ),
            menus = listOf(
                MenuResponse(1L, "치킨 세트", "SET", 25000, 0),
                MenuResponse(2L, "생맥주 1L", "DRINK", 8000, 1),
                MenuResponse(3L, "피자 (L)", "FOOD", 22000, 2),
            ),
        )

        private val mockMapItems = listOf(
            PubMapItemResponse(1L, "시그니처 펍", 37.5516, 126.9086, "OPEN", 24),
            PubMapItemResponse(2L, "야구 천국", 37.5100, 127.0800, "OPEN", 18),
            PubMapItemResponse(3L, "응원 펍 잠실", 37.5113, 127.0730, "CLOSED", 9),
        )

        // ──────────────────────────────────────────────────────────────
        // 구현
        // ──────────────────────────────────────────────────────────────

        override suspend fun getPubs(
            keyword: String?,
            teamId: Long?,
            teamIds: List<Long>?,
            region: String?,
            facilityCodes: List<String>?,
            styleCodes: List<String>?,
            themeCodes: List<String>?,
            foodCodes: List<String>?,
            capacityRange: String?,
            openNow: Boolean?,
            businessDay: String?,
            page: Int?,
            size: Int?,
        ): BaseResponse<GetPubsResponse> {
            if (BuildConfig.USE_MOCK_SERVER) {
                var filtered = mockListItems
                if (!keyword.isNullOrBlank()) {
                    filtered = filtered.filter {
                        it.name.contains(keyword, ignoreCase = true) ||
                            it.address.contains(keyword, ignoreCase = true)
                    }
                }
                val effectiveTeamIds = teamIds?.takeIf { it.isNotEmpty() }
                    ?: listOfNotNull(teamId)
                if (effectiveTeamIds.isNotEmpty()) {
                    filtered = filtered.filter { item ->
                        item.supportedTeams.any { it.teamId in effectiveTeamIds }
                    }
                }
                if (!region.isNullOrBlank()) {
                    filtered = filtered.filter { it.region == region }
                }
                if (openNow == true) {
                    filtered = filtered.filter { it.businessStatus == "OPEN_NOW" }
                }
                return BaseResponse(
                    status = "success_mock",
                    statusCode = 200,
                    data = GetPubsResponse(
                        content = filtered,
                        page = page ?: 0,
                        size = size ?: 20,
                        totalElements = filtered.size,
                        totalPages = 1,
                    ),
                    timestamp = "",
                )
            }
            return pubService.getPubs(
                keyword,
                teamId,
                teamIds,
                region,
                facilityCodes,
                styleCodes,
                themeCodes,
                foodCodes,
                capacityRange,
                openNow,
                businessDay,
                page,
                size,
            )
        }

        override suspend fun getMapPubs(
            swLat: Double,
            swLng: Double,
            neLat: Double,
            neLng: Double,
            teamId: Long?,
        ): BaseResponse<GetPubsMapResponse> {
            if (BuildConfig.USE_MOCK_SERVER) {
                val filtered = if (teamId != null) {
                    // 지도 mock에서는 teamId 필터 무시 (마커 단순 반환)
                    mockMapItems
                } else {
                    mockMapItems
                }
                return BaseResponse(
                    status = "success_mock",
                    statusCode = 200,
                    data = GetPubsMapResponse(pubs = filtered),
                    timestamp = "",
                )
            }
            return pubService.getMapPubs(swLat, swLng, neLat, neLng, teamId)
        }

        override suspend fun getPubDetail(pubId: Long): BaseResponse<PubDetailResponse> {
            if (BuildConfig.USE_MOCK_SERVER) {
                val detail = if (pubId == mockDetail.pubId) {
                    mockDetail
                } else {
                    mockDetail.copy(pubId = pubId, name = "펍 $pubId")
                }
                return BaseResponse(
                    status = "success_mock",
                    statusCode = 200,
                    data = detail,
                    timestamp = "",
                )
            }
            return pubService.getPubDetail(pubId)
        }
    }
