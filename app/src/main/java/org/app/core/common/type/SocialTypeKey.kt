package org.app.core.common.type

import dagger.MapKey
import org.app.presentation.onboarding.login.LoginContract

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SocialTypeKey(
    val value: LoginContract.SocialType,
)
