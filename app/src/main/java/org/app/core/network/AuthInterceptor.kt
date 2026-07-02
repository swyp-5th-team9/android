package org.app.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.app.data.local.datasource.api.LocalTokenDataSource
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val localTokenDataSource: LocalTokenDataSource,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val accessToken = runBlocking { localTokenDataSource.getAccessToken() }

            val newRequest =
                chain
                    .request()
                    .newBuilder()
                    .apply {
                        if (!accessToken.isNullOrBlank()) {
                            addHeader(AUTHORIZATION, "$BEARER $accessToken")
                        }
                    }.build()

            return chain.proceed(newRequest)
        }

        companion object {
            private const val AUTHORIZATION = "Authorization"
            private const val BEARER = "Bearer"
        }
    }
