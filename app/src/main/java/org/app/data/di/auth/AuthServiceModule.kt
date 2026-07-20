package org.app.data.di.auth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.core.network.di.RefreshRetrofit
import org.app.data.remote.service.AuthService
import org.app.data.remote.service.TokenRefreshService
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create()

    @Provides
    @Singleton
    fun provideTokenRefreshService(
        @RefreshRetrofit retrofit: Retrofit,
    ): TokenRefreshService = retrofit.create()
}
