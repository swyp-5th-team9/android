package org.app.data.di.report

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.datasource.api.ReportRemoteDataSource
import org.app.data.remote.datasource.impl.ReportRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindReportRemoteDataSource(impl: ReportRemoteDataSourceImpl): ReportRemoteDataSource
}
