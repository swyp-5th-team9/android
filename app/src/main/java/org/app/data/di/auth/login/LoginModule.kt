package org.app.data.di.auth.login

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import org.app.core.common.type.SocialTypeKey
import org.app.presentation.onboarding.login.LoginContract
import org.app.presentation.onboarding.login.SocialLoginManager
import org.app.presentation.onboarding.login.kakao.KakaoLoginManager
import org.app.presentation.onboarding.login.naver.NaverLoginManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {
    @Binds
    @Singleton
    @IntoMap
    @SocialTypeKey(LoginContract.SocialType.KAKAO)
    abstract fun bindKakaoLoginManager(kakaoLoginManager: KakaoLoginManager): SocialLoginManager

    @Binds
    @Singleton
    @IntoMap
    @SocialTypeKey(LoginContract.SocialType.NAVER)
    abstract fun bindNaverLoginManager(naverLoginManager: NaverLoginManager): SocialLoginManager
}
