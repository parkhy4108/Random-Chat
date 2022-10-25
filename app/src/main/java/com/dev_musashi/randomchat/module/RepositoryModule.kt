package com.dev_musashi.randomchat.module

import android.content.Context
import com.dev_musashi.randomchat.data.repositoryImpl.RepositoryImpl
import com.dev_musashi.randomchat.domain.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(
        @ApplicationContext context: Context
    ): Repository = RepositoryImpl(context)

}