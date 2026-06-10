package org.app.data.remote.datasource.impl

import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import org.app.core.util.suspendRunCatching
import org.app.data.remote.datasource.api.KakaoAuthRemoteDataSource
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoAuthRemoteDataSourceImpl
    @Inject
    constructor() : KakaoAuthRemoteDataSource {
        override suspend fun logoutKakao(): Result<Unit> =
            suspendRunCatching {
                suspendCancellableCoroutine { continuation ->
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            continuation.resumeWithException(error)
                        } else {
                            continuation.resume(Unit)
                        }
                    }
                }
            }

        override suspend fun withdrawKakao(): Result<Unit> =
            suspendRunCatching {
                suspendCancellableCoroutine { continuation ->
                    UserApiClient.instance.unlink { error ->
                        if (error != null) {
                            continuation.resumeWithException(error)
                        } else {
                            continuation.resume(Unit)
                        }
                    }
                }
            }
    }
