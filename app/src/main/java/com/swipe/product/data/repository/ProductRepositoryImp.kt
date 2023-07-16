package com.swipe.product.data.repository

import com.google.gson.JsonElement
import com.swipe.product.data.source.remote.ApiService
import com.swipe.product.domain.model.Product
import com.swipe.product.domain.repository.ProductRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProductRepositoryImp(private val apiService: ApiService) :
    ProductRepository {

    override suspend fun getProducts(): ArrayList<Product> {
        return apiService.getProduct()
    }

    override suspend fun addProduct(product: Product): JsonElement {
        //fetch file
        val file: MultipartBody.Part? = if (product.file != null) {
            MultipartBody.Part.createFormData(
                "files[]",
                product.file!!.name,
                product.file!!.asRequestBody("image/*".toMediaType())
            )
        } else {
            null
        }
        return apiService.addProduct(
            product.name.toRequestBody("text/plain;charset=utf-8".toMediaType()),
            product.type.toRequestBody("text/plain;charset=utf-8".toMediaType()),
            product.price.toRequestBody("text/plain;charset=utf-8".toMediaType()),
            product.tax.toRequestBody("text/plain;charset=utf-8".toMediaType()),
            file
        )
    }
}