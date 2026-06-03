package org.app.data.di.dummy

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.repository.api.DummyRepository
import org.app.data.repository.impl.DummyRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DummyRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDummyRepository(dummyRepositoryImpl: DummyRepositoryImpl): DummyRepository
}
