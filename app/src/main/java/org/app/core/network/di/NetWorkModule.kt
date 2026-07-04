package org.app.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.moball.app.BuildConfig
import com.moball.app.BuildConfig.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.app.core.network.AuthInterceptor
import org.app.core.network.TokenAuthenticator
import org.app.core.network.isJsonArray
import org.app.core.network.isJsonObject
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshRetrofit

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
    @LoggingInterceptor
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
    fun provideOkHttpClient(
        @LoggingInterceptor loggingInterceptor: Interceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
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

    // refresh-token 전용 클라이언트: authenticator/authInterceptor 없이 구성해
    // 토큰 갱신 요청이 본 클라이언트의 Dispatcher 큐에 갇히는 데드락을 방지한다.
    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshOkHttpClient(
        @LoggingInterceptor loggingInterceptor: Interceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshRetrofit(
        @RefreshClient client: OkHttpClient,
        factory: Converter.Factory,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(factory)
            .build()
}
