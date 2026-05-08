package com.mars.madereraapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

import kotlinx.coroutines.flow.firstOrNull

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://192.168.1.44:3000/api/" // IP de la PC en la red local

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: com.mars.madereraapp.data.SessionManager): okhttp3.Interceptor {
        return okhttp3.Interceptor { chain ->
            val token: String? = kotlinx.coroutines.runBlocking {
                sessionManager.token.firstOrNull()
            }
            val requestBuilder = chain.request().newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: okhttp3.Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                // Si el servidor responde 401, el token venció — emitir evento global
                if (response.code == 401) {
                    UnauthorizedEventBus.emit()
                }
                response
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): com.mars.madereraapp.data.remote.AuthApiService {
        return retrofit.create(com.mars.madereraapp.data.remote.AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCatalogApiService(retrofit: Retrofit): com.mars.madereraapp.data.remote.CatalogApiService {
        return retrofit.create(com.mars.madereraapp.data.remote.CatalogApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRequerimientoApiService(retrofit: Retrofit): com.mars.madereraapp.data.remote.RequerimientoApiService {
        return retrofit.create(com.mars.madereraapp.data.remote.RequerimientoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideIngresoApiService(retrofit: Retrofit): com.mars.madereraapp.data.remote.IngresoApiService {
        return retrofit.create(com.mars.madereraapp.data.remote.IngresoApiService::class.java)
    }
}
