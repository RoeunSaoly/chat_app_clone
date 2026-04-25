package com.example.chat_app_clone.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/"

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
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
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
