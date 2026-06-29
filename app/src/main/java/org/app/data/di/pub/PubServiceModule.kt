package org.app.data.di.pub

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.service.PubService
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PubServiceModule {
    @Provides
    @Singleton
    fun providePubService(retrofit: Retrofit): PubService = retrofit.create()
}
