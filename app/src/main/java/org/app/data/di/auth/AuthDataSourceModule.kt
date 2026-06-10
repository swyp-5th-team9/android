package org.app.data.di.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.local.datasource.api.LocalTokenDataSource
import org.app.data.local.datasource.impl.LocalTokenDataSourceImpl
import org.app.data.remote.datasource.api.AuthRemoteDataSource
import org.app.data.remote.datasource.api.KakaoAuthDataSource
import org.app.data.remote.datasource.api.NaverAuthDataSource
import org.app.data.remote.datasource.impl.AuthRemoteDataSourceImpl
import org.app.data.remote.datasource.impl.KakaoAuthDataSourceImpl
import org.app.data.remote.datasource.impl.NaverAuthDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindKakaoAuthDataSource(kakaoAuthDataSourceImpl: KakaoAuthDataSourceImpl): KakaoAuthDataSource

    @Binds
    @Singleton
    abstract fun bindNaverAuthDataSource(naverAuthDataSourceImpl: NaverAuthDataSourceImpl): NaverAuthDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(authRemoteDataSourceImpl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLocalTokenDataSource(localTokenDataSourceImpl: LocalTokenDataSourceImpl): LocalTokenDataSource
}
