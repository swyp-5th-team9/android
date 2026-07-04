package org.app.core.network

import dagger.Lazy
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.app.data.local.datasource.api.LocalTokenDataSource
import org.app.data.repository.api.AuthRepository
import timber.log.Timber
import javax.inject.Inject

class TokenAuthenticator
    @Inject
    constructor(
        private val authRepository: Lazy<AuthRepository>,
        private val localTokenDataSource: LocalTokenDataSource,
        private val authManager: AuthManager,
    ) : Authenticator {
        private val mutex = Mutex()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            // refresh-token 요청 자체가 401이면 재시도 금지 (mutex deadlock 방지)
            if (response.request.url.pathSegments
                    .contains("refresh-token")
            ) {
                return null
            }
            if (responseCount(response) >= MAX_RESPONSE_COUNT) return null
            return runBlocking { updateToken(response) }
        }

        private suspend fun updateToken(response: Response): Request? =
            mutex.withLock {
                val accessToken = localTokenDataSource.getAccessToken()
                val oldAccessToken = response.request.header(AUTHORIZATION)?.removePrefix("$BEARER ")

                // 다른 스레드에서 이미 토큰 갱신된 경우 → 갱신된 토큰으로 바로 재요청
                if (accessToken != null && accessToken != oldAccessToken) {
                    return response.request
                        .newBuilder()
                        .header(AUTHORIZATION, "$BEARER $accessToken")
                        .build()
                }

                var newAccessToken: String? = null
                authRepository
                    .get()
                    .refreshToken()
                    .onSuccess { token ->
                        Timber.tag(TAG).d("토큰 재발급 성공")
                        newAccessToken = token
                    }.onFailure { error ->
                        Timber.tag(TAG).e("토큰 재발급 실패: ${error.message}")
                        handleReissueFailure()
                        return null
                    }

                response.request
                    .newBuilder()
                    .header(AUTHORIZATION, "$BEARER $newAccessToken")
                    .build()
            }

        private suspend fun handleReissueFailure() {
            localTokenDataSource.clearTokens()
            authManager.emitAuthEvent()
        }

        private fun responseCount(response: Response): Int {
            var count = 1
            var priorResponse = response.priorResponse
            while (priorResponse != null) {
                count++
                priorResponse = priorResponse.priorResponse
            }
            return count
        }

        companion object {
            private const val MAX_RESPONSE_COUNT = 2
            private const val AUTHORIZATION = "Authorization"
            private const val BEARER = "Bearer"
            private const val TAG = "TokenAuthenticator"
        }
    }
