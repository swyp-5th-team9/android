package org.app.data.repository.api

import org.app.data.model.DummyUser

interface DummyRepository {
    suspend fun fetchDummyUserList(page: Int): Result<List<DummyUser>>
}
