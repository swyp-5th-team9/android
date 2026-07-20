package org.app.presentation.onboarding.login

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.domain.model.SocialType

/**
 * Composable(UI 레이어)에서 소셜 로그인 SDK 매니저를 꺼내오기 위한 EntryPoint.
 *
 * SDK 로그인은 Activity Context가 필요한 UI 관심사이므로 Screen에서 수행하고,
 * ViewModel에는 결과 토큰만 전달한다. (ViewModel에 Context를 넘기지 않기 위함)
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface SocialLoginManagerEntryPoint {
    fun socialLoginManagers(): Map<SocialType, @JvmSuppressWildcards SocialLoginManager>
}
