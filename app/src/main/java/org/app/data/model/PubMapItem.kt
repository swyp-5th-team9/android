package org.app.data.model

data class PubMapItem(
    val pubId: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val favoriteCount: Int,
)
