package org.app.data.remote.datasource.impl

import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import org.app.core.util.suspendRunCatching
import org.app.data.remote.datasource.api.NaverAuthRemoteDataSource
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NaverAuthRemoteDataSourceImpl
    @Inject
    constructor() : NaverAuthRemoteDataSource {
        override suspend fun logoutNaver(): Result<Unit> =
            suspendRunCatching {
                suspendCancellableCoroutine { continuation ->
                    NidOAuth.logout(
                        object : NidOAuthCallback {
                            override fun onSuccess() {
                                continuation.resume(Unit)
                            }

                            override fun onFailure(
                                errorCode: String,
                                errorDesc: String,
                            ) {
                                continuation.resumeWithException(
                                    RuntimeException("Naver logout failed: $errorCode - $errorDesc"),
                                )
                            }
                        },
                    )
                }
            }

        override suspend fun withdrawNaver(): Result<Unit> =
            suspendRunCatching {
                suspendCancellableCoroutine { continuation ->
                    NidOAuth.disconnect(
                        object : NidOAuthCallback {
                            override fun onSuccess() {
                                continuation.resume(Unit)
                            }

                            override fun onFailure(
                                errorCode: String,
                                errorDesc: String,
                            ) {
                                continuation.resumeWithException(
                                    RuntimeException("Naver withdraw failed: $errorCode - $errorDesc"),
                                )
                            }
                        },
                    )
                }
            }
    }
