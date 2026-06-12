package org.app.presentation.onboarding.login.naver

import android.content.Context
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import org.app.core.util.suspendRunCatching
import org.app.presentation.onboarding.login.SocialLoginManager
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NaverLoginManager
    @Inject
    constructor() : SocialLoginManager {
        override suspend fun login(context: Context): Result<String> =
            suspendRunCatching {
                suspendCancellableCoroutine { continuation ->
                    val callback =
                        object : NidOAuthCallback {
                            override fun onSuccess() {
                                val token = NidOAuth.getAccessToken()
                                if (token != null) {
                                    continuation.resume(token)
                                } else {
                                    continuation.resumeWithException(
                                        IllegalStateException("Naver access token is null"),
                                    )
                                }
                            }

                            override fun onFailure(
                                errorCode: String,
                                errorDesc: String,
                            ) {
                                continuation.resumeWithException(
                                    RuntimeException("Naver login failed: $errorCode - $errorDesc"),
                                )
                            }
                        }
                    NidOAuth.requestLogin(context, callback)
                }
            }
    }
