package com.swipe.product.data.source.remote

import com.google.gson.JsonElement
import com.swipe.product.domain.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @GET("get")
    suspend fun getProduct(): ArrayList<Product>

    @Multipart
    @POST("add")
    suspend fun addProduct(
        @Part(value = "product_name") name: RequestBody,
        @Part(value = "product_type") type: RequestBody,
        @Part(value = "price") price: RequestBody,
        @Part(value = "tax") tax: RequestBody,
        @Part file: MultipartBody.Part?
    ) : JsonElement
}