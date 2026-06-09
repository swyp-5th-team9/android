package org.app.presentation.login

import android.content.Context

interface SocialLoginManager {
    suspend fun login(context: Context): Result<String>
}
