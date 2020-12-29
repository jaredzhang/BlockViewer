package com.jaredzhang.blockviewer.di

import com.jaredzhang.blockviewer.BuildConfig
import com.jaredzhang.blockviewer.api.ChainService

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {

    @Provides
    @ApplicationScope
    fun providesOkHttpClient(): OkHttpClient {

        val client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
            )

        return client.build()
    }

    @Provides
    @ApplicationScope
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.testnet.eos.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @ApplicationScope
    fun provideChainService(retrofit: Retrofit): ChainService {
        return retrofit.create(ChainService::class.java)
    }
}