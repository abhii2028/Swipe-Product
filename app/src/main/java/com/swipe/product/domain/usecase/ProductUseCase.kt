package com.swipe.product.domain.usecase

import com.google.gson.JsonElement
import com.swipe.product.domain.model.Product
import com.swipe.product.domain.repository.ProductRepository
import com.swipe.product.domain.usecase.base.UseCase

class ProductUseCase constructor(
    private val productRepository: ProductRepository
) : UseCase<Any?>() {

    override suspend fun get(params: Any?): ArrayList<Product> {
        return productRepository.getProducts()
    }

    override suspend fun add(params: Any?): JsonElement {
        return productRepository.addProduct(params as Product)
    }

}