package org.app.data.remote.datasource.impl

import org.app.data.remote.datasource.api.DummyDataSource
import org.app.data.remote.dto.GetUserListResponse
import org.app.data.remote.service.DummyService
import javax.inject.Inject

class DummyDataSourceImpl
    @Inject
    constructor(
        private val dummyService: DummyService,
    ) : DummyDataSource {
        override suspend fun getDummyUserList(page: Int): GetUserListResponse = dummyService.getUserList(page = page)
    }
