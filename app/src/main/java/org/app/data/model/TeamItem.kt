package org.app.data.model

/**
 * 구단 도메인 모델.
 * GET /api/v1/teams 응답 기반 — 하드코딩 매핑 대신 이 모델을 사용한다.
 */
data class TeamItem(
    val teamId: Long,
    val name: String,
    val shortName: String,
    val sportType: String,
    val homeStadium: String?,
)
