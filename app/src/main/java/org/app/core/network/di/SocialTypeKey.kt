package org.app.core.network.di

import dagger.MapKey
import org.app.domain.model.SocialType

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SocialTypeKey(
    val value: SocialType,
)
