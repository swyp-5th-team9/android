package org.app.data.di.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.local.datasource.api.LocalTokenDataSource
import org.app.data.local.datasource.impl.LocalTokenDataSourceImpl
import org.app.data.remote.datasource.api.AuthRemoteDataSource
import org.app.data.remote.datasource.api.KakaoAuthRemoteDataSource
import org.app.data.remote.datasource.api.NaverAuthRemoteDataSource
import org.app.data.remote.datasource.impl.AuthRemoteDataSourceImpl
import org.app.data.remote.datasource.impl.KakaoAuthRemoteDataSourceImpl
import org.app.data.remote.datasource.impl.NaverAuthRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindKakaoAuthRemoteDataSource(
        kakaoAuthRemoteDataSourceImpl: KakaoAuthRemoteDataSourceImpl,
    ): KakaoAuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindNaverAuthRemoteDataSource(
        naverAuthRemoteDataSourceImpl: NaverAuthRemoteDataSourceImpl,
    ): NaverAuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(authRemoteDataSourceImpl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLocalTokenDataSource(localTokenDataSourceImpl: LocalTokenDataSourceImpl): LocalTokenDataSource
}
