package com.swipe.product.domain.repository

import com.google.gson.JsonElement
import com.swipe.product.domain.model.Product

interface ProductRepository {

    suspend fun getProducts(): ArrayList<Product>
    suspend fun addProduct(product: Product) : JsonElement
}