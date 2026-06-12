package org.app.presentation.onboarding.login

import android.content.Context

interface SocialLoginManager {
    suspend fun login(context: Context): Result<String>
}
