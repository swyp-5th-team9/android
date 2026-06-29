package org.app.data.di.report

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.service.ReportService
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportServiceModule {
    @Provides
    @Singleton
    fun provideReportService(retrofit: Retrofit): ReportService = retrofit.create()
}
