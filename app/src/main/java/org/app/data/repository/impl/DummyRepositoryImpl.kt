package org.app.data.repository.impl

import org.app.core.util.suspendRunCatching
import org.app.data.mapper.toDummyUser
import org.app.data.model.DummyUser
import org.app.data.remote.datasource.api.DummyDataSource
import org.app.data.repository.api.DummyRepository
import javax.inject.Inject

class DummyRepositoryImpl
    @Inject
    constructor(
        private val dummyDataSource: DummyDataSource,
    ) : DummyRepository {
        override suspend fun fetchDummyUserList(page: Int): Result<List<DummyUser>> =
            suspendRunCatching {
                val response = dummyDataSource.getDummyUserList(page)
                response.data?.map { it.toDummyUser() } ?: emptyList()
            }
    }
