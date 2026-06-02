package org.app.data.di.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.local.datasource.api.LocalTokenDataSource
import org.app.data.local.datasource.impl.LocalTokenDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindLocalTokenDataSource(localTokenDataSourceImpl: LocalTokenDataSourceImpl): LocalTokenDataSource
}
