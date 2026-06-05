package com.example.chat_app_clone.di

import android.content.Context
import com.example.chat_app_clone.data.PreferenceManager
import com.example.chat_app_clone.network.SocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }

    @Provides
    @Singleton
    fun provideSocketManager(): SocketManager {
        return SocketManager() 
    }
}
