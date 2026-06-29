package org.app.data.di.favorite

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.datasource.api.FavoriteRemoteDataSource
import org.app.data.remote.datasource.impl.FavoriteRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoriteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindFavoriteRemoteDataSource(impl: FavoriteRemoteDataSourceImpl): FavoriteRemoteDataSource
}
