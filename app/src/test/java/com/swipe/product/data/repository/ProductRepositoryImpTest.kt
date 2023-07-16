package com.swipe.product.data.repository

import com.swipe.product.domain.model.Product
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test


class ProductRepositoryImpTest {

    @MockK
    lateinit var productRepositoryImp: ProductRepositoryImp

    @Before
    fun setUp() {
        MockKAnnotations.init(this) //for initialization
    }

    @Test
    fun getPostsData() = runBlocking {
        val products = mockk<ArrayList<Product>>()
        every { runBlocking { productRepositoryImp.getProducts() } } returns (products)

        val result = productRepositoryImp.getProducts()
        MatcherAssert.assertThat(
            "Received result [$result] & mocked [$products] must be matches on each other!",
            result,
            CoreMatchers.`is`(products)
        )
    }
}