package org.app.data.di.dummy

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.app.data.remote.datasource.api.DummyDataSource
import org.app.data.remote.datasource.impl.DummyDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DummyDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindDummyDataSource(dummyDataSourceImpl: DummyDataSourceImpl): DummyDataSource
}
