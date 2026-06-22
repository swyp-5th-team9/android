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
            val request = chain.request()
            val path = request.url.encodedPath

            if (path.contains("/auth/login/") || path.contains("/auth/refresh-token")) {
                return chain.proceed(request)
            }

            val token = runBlocking { localTokenDataSource.getAccessToken() }

            val newRequest =
                if (token != null) {
                    request
                        .newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    request
                }

            return chain.proceed(newRequest)
        }
    }
