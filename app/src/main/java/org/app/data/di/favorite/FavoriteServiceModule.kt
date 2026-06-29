package org.app.data.di.favorite

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.service.FavoriteService
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FavoriteServiceModule {
    @Provides
    @Singleton
    fun provideFavoriteService(retrofit: Retrofit): FavoriteService = retrofit.create()
}
