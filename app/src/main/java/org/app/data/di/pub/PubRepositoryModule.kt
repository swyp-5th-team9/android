package org.app.data.di.pub

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.repository.api.PubRepository
import org.app.data.repository.impl.PubRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PubRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPubRepository(impl: PubRepositoryImpl): PubRepository
}
