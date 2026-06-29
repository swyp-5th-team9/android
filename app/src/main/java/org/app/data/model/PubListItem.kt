package org.app.data.model

data class PubListItem(
    val pubId: Long,
    val name: String,
    val region: String,
    val address: String,
    val thumbnailUrl: String?,
    val favoriteCount: Int,
    val businessStatus: String,
    val supportedTeams: List<PubTeam>,
    val facilityCodes: List<String>,
    val styleCodes: List<String>,
    val themeCodes: List<String>,
    val foodCodes: List<String>,
)

data class PubTeam(
    val teamId: Long,
    val shortName: String,
    val name: String?,
)

data class PubPage(
    val content: List<PubListItem>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
)
