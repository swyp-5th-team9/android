package org.app.data.di.notification

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.service.NotificationService
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationServiceModule {
    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationService = retrofit.create()
}
