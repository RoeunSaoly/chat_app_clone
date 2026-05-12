package com.example.chat_app_clone.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val TAG = "ChatApi"

    @Volatile
    private var token: String? = null

    fun setAuthToken(authToken: String?) {
        token = authToken
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                token?.let {
                    request.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(request.build())
            }
            .authenticator { _, response ->
                // This runs when we get a 401
                Log.d(TAG, "Token expired, attempting refresh...")
                val refreshToken = getTokenProvider?.invoke() ?: return@authenticator null
                
                // Create a separate Retrofit instance for refresh to avoid circular dependency
                val refreshRetrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val refreshApi = refreshRetrofit.create(AuthApi::class.java)

                // Synchronous call to refresh
                val refreshResponse = refreshApi.refreshSync(com.example.chat_app_clone.network.model.RefreshRequest(refreshToken)).execute()
                val body = refreshResponse.body()
                if (refreshResponse.isSuccessful && body != null && body.accessToken != null) {
                    val newAccess = body.accessToken
                    val newRefresh = body.refreshToken ?: refreshToken
                    
                    setAuthToken(newAccess)
                    onTokenRefreshed?.invoke(newAccess, newRefresh)
                    
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccess")
                        .build()
                } else {
                    null // Give up, back to login
                }
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private var getTokenProvider: (() -> String?)? = null
    private var onTokenRefreshed: ((String, String) -> Unit)? = null

    fun init(tokenProvider: () -> String?, refreshListener: (String, String) -> Unit) {
        this.getTokenProvider = tokenProvider
        this.onTokenRefreshed = refreshListener
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.API_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Volatile
    private var retrofitInstance: Retrofit? = null

    val retrofit: Retrofit
        get() {
            return retrofitInstance ?: synchronized(this) {
                retrofitInstance ?: createRetrofit().also { retrofitInstance = it }
            }
        }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val chatApi: ChatApi by lazy { retrofit.create(ChatApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
}
