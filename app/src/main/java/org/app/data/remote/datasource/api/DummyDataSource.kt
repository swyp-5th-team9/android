package org.app.data.remote.datasource.api

import org.app.data.remote.dto.GetUserListResponse

interface DummyDataSource {
    suspend fun getDummyUserList(page: Int): GetUserListResponse
}
