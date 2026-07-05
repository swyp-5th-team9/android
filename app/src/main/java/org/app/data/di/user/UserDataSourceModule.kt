package org.app.data.di.user

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.local.datasource.api.LocalProfileImageDataSource
import org.app.data.local.datasource.impl.LocalProfileImageDataSourceImpl
import org.app.data.remote.datasource.api.UserRemoteDataSource
import org.app.data.remote.datasource.impl.UserRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(userRemoteDataSourceImpl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLocalProfileImageDataSource(
        localProfileImageDataSourceImpl: LocalProfileImageDataSourceImpl,
    ): LocalProfileImageDataSource
}
