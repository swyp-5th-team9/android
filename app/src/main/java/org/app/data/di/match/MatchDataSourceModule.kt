package org.app.data.di.match

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.datasource.api.MatchRemoteDataSource
import org.app.data.remote.datasource.impl.MatchRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MatchDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindMatchRemoteDataSource(impl: MatchRemoteDataSourceImpl): MatchRemoteDataSource
}
