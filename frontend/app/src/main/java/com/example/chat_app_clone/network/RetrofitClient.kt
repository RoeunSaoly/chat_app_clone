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
                val request = chain.request()
                try {
                    val response = chain.proceed(request)
                    Log.d(TAG, "${request.method} ${request.url} -> HTTP ${response.code}")
                    response
                } catch (e: IOException) {
                    Log.e(TAG, "Server unreachable at ${NetworkConfig.API_BASE_URL}", e)
                    throw IOException(
                        "Server unreachable. Make sure your phone and PC are on the same WiFi, backend is running on ${NetworkConfig.API_BASE_URL}, and Windows Firewall allows port 3000.",
                        e
                    )
                }
            }
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                token?.let {
                    request.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(request.build())
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
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

    fun rebuild() {
        synchronized(this) {
            retrofitInstance = createRetrofit()
        }
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val chatApi: ChatApi by lazy { retrofit.create(ChatApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
}
