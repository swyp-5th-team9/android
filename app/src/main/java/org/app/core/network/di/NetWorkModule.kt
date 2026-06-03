package org.app.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.app.BuildConfig
import org.app.BuildConfig.BASE_URL
import org.app.core.network.isJsonArray
import org.app.core.network.isJsonObject
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val CONTENT_TYPE = "application/json"
    private const val LOGGING_TAG = "okhttp"

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            prettyPrint = BuildConfig.DEBUG
        }

    @Provides
    @Singleton
    fun provideJsonConverter(json: Json): Converter.Factory = json.asConverterFactory(CONTENT_TYPE.toMediaType())

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor { message ->
            when {
                message.isJsonObject() -> {
                    runCatching { JSONObject(message).toString(4) }
                        .onSuccess { Timber.tag(LOGGING_TAG).d(it) }
                        .onFailure { Timber.tag(LOGGING_TAG).d(message) }
                }

                message.isJsonArray() -> {
                    runCatching { JSONObject(message).toString(4) }
                        .onSuccess { Timber.tag(LOGGING_TAG).d(it) }
                        .onFailure { Timber.tag(LOGGING_TAG).d(message) }
                }

                else -> {
                    Timber.tag(LOGGING_TAG).d("CONNECTION INFO -> $message")
                }
            }
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: Interceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        factory: Converter.Factory,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(factory)
            .build()
}
