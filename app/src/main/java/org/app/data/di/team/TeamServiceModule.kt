package org.app.data.di.team

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.service.TeamService
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TeamServiceModule {
    @Provides
    @Singleton
    fun provideTeamService(retrofit: Retrofit): TeamService = retrofit.create()
}
