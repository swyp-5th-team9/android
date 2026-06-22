package org.app.presentation.onboarding.login.naver

import android.content.Context
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import org.app.core.util.suspendRunCatching
import org.app.presentation.onboarding.login.SocialLoginManager
import timber.log.Timber
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
                                    Timber.d("[Naver] SDK 액세스토큰: $token")
                                    continuation.resume(token)
                                } else {
                                    Timber.e("[Naver] SDK 액세스토큰 null")
                                    continuation.resumeWithException(
                                        IllegalStateException("Naver access token is null"),
                                    )
                                }
                            }

                            override fun onFailure(
                                errorCode: String,
                                errorDesc: String,
                            ) {
                                Timber.e("[Naver] 로그인 실패: $errorCode - $errorDesc")
                                continuation.resumeWithException(
                                    RuntimeException("Naver login failed: $errorCode - $errorDesc"),
                                )
                            }
                        }
                    NidOAuth.requestLogin(context, callback)
                }
            }
    }
