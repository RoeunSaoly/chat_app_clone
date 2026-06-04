package com.example.chat_app_clone.di

import com.example.chat_app_clone.data.PreferenceManager
import com.example.chat_app_clone.network.AuthApi
import com.example.chat_app_clone.network.ChatApi
import com.example.chat_app_clone.network.NetworkConfig
import com.example.chat_app_clone.network.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import android.util.Log
import com.example.chat_app_clone.network.SocketManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(preferenceManager: PreferenceManager, socketManager: SocketManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val token = preferenceManager.getAccessToken()
                token?.let {
                    request.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(request.build())
            }
            .authenticator { _, response ->
                val refreshToken = preferenceManager.getRefreshToken() ?: return@authenticator null
                
                val refreshRetrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val refreshApi = refreshRetrofit.create(AuthApi::class.java)

                try {
                    val refreshResponse = refreshApi.refreshSync(com.example.chat_app_clone.network.model.RefreshRequest(refreshToken)).execute()
                    val body = refreshResponse.body()
                    if (refreshResponse.isSuccessful && body != null && body.accessToken != null) {
                        val newAccess = body.accessToken
                        val newRefresh = body.refreshToken ?: refreshToken
                        
                        preferenceManager.saveTokens(newAccess, newRefresh)
                        socketManager.disconnect()
                        socketManager.connectSocket(newAccess)
                        
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccess")
                            .build()
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi {
        return retrofit.create(ChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}
