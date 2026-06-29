package org.app.data.di.pub

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.datasource.api.PubRemoteDataSource
import org.app.data.remote.datasource.impl.PubRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PubDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindPubRemoteDataSource(impl: PubRemoteDataSourceImpl): PubRemoteDataSource
}
