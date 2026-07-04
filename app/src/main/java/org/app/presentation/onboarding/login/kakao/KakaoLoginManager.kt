package org.app.presentation.onboarding.login.kakao

import android.content.Context
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import org.app.core.util.suspendRunCatching
import org.app.presentation.onboarding.login.SocialLoginManager
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoLoginManager
    @Inject
    constructor() : SocialLoginManager {
        override suspend fun login(context: Context): Result<String> =
            suspendRunCatching {
                getKakaoAccessToken(context)
            }

        private suspend fun getKakaoAccessToken(context: Context): String =
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                try {
                    loginWithKakaoTalk(context)
                } catch (error: Exception) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        throw error
                    }
                    loginWithKakaoAccount(context)
                }
            } else {
                loginWithKakaoAccount(context)
            }

        private suspend fun loginWithKakaoTalk(context: Context): String =
            suspendCancellableCoroutine { continuation ->
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Timber.e(error, "[Kakao] KakaoTalk 로그인 실패")
                        continuation.resumeWithException(error)
                        return@loginWithKakaoTalk
                    } else if (token != null) {
                        Timber.d("[Kakao] KakaoTalk SDK 로그인 성공")
                        continuation.resume(token.accessToken)
                    }
                }
            }

        private suspend fun loginWithKakaoAccount(context: Context): String =
            suspendCancellableCoroutine { continuation ->
                UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                    if (error != null) {
                        Timber.e(error, "[Kakao] 카카오계정 로그인 실패")
                        continuation.resumeWithException(error)
                        return@loginWithKakaoAccount
                    } else if (token != null) {
                        Timber.d("[Kakao] 카카오계정 SDK 로그인 성공")
                        continuation.resume(token.accessToken)
                    }
                }
            }
    }
