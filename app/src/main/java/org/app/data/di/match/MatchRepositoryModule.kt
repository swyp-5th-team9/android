package org.app.data.di.match

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.repository.api.MatchRepository
import org.app.data.repository.impl.MatchRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MatchRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository
}
