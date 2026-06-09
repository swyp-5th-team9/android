package org.app.presentation.login

import android.content.Context
import org.app.core.util.suspendRunCatching
import javax.inject.Inject

class NaverLoginManager
    @Inject
    constructor() : SocialLoginManager {
        override suspend fun login(context: Context): Result<String> =
            suspendRunCatching {
                // TODO: 네이버 로그인 SDK 구현 예정
                "fake_naver_token"
            }
    }
