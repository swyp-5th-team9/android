package org.app.data.repository.impl

import android.net.Uri
import org.app.core.util.suspendRunCatching
import org.app.data.mapper.toReportResult
import org.app.data.model.ReportResult
import org.app.data.remote.datasource.api.ReportRemoteDataSource
import org.app.data.remote.dto.getDataOrThrow
import org.app.data.repository.api.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl
    @Inject
    constructor(
        private val reportRemoteDataSource: ReportRemoteDataSource,
    ) : ReportRepository {
        override suspend fun postReport(
            category: String,
            content: String,
            pubId: Long?,
            imageUris: List<Uri>,
        ): Result<ReportResult> =
            suspendRunCatching {
                reportRemoteDataSource
                    .postReport(category, content, pubId, imageUris)
                    .getDataOrThrow()
                    .toReportResult()
            }
    }
