package org.app.core.common

import dagger.MapKey
import org.app.presentation.login.LoginContract

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SocialTypeKey(
    val value: LoginContract.SocialType,
)
