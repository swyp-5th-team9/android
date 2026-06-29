package org.app.data.di.team

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.datasource.api.TeamRemoteDataSource
import org.app.data.remote.datasource.impl.TeamRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TeamDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindTeamRemoteDataSource(impl: TeamRemoteDataSourceImpl): TeamRemoteDataSource
}
