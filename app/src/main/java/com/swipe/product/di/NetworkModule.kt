package com.swipe.product.di

import com.google.gson.GsonBuilder
import com.swipe.product.BuildConfig
import com.swipe.product.data.repository.ProductRepositoryImp
import com.swipe.product.data.source.remote.ApiService
import com.swipe.product.domain.repository.ProductRepository
import com.swipe.product.domain.usecase.ProductUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TIME_OUT = 30L

val NetworkModule = module {

    single { createService(get()) }

    single { createRetrofit(get(), BuildConfig.BASE_URL) }

    single { createOkHttpClient() }

}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor).build()
}

fun createRetrofit(okHttpClient: OkHttpClient, url: String): Retrofit {
    val gson = GsonBuilder().setLenient().create()
    return Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

fun createService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}

fun createProductRepository(apiService: ApiService): ProductRepository {
    return ProductRepositoryImp(apiService)
}

fun createProductUseCase(productRepository: ProductRepository): ProductUseCase {
    return ProductUseCase(productRepository)
}
