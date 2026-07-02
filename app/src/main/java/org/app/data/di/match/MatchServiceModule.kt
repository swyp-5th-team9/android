package org.app.data.di.match

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.service.MatchService
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MatchServiceModule {
    @Provides
    @Singleton
    fun provideMatchService(retrofit: Retrofit): MatchService = retrofit.create()
}
