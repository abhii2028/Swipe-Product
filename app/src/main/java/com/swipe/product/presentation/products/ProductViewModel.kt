package com.swipe.product.presentation.products

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.swipe.product.domain.model.ApiError
import com.swipe.product.domain.model.Product
import com.swipe.product.domain.usecase.ProductUseCase
import com.swipe.product.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.cancel


class ProductViewModel constructor(private val productUseCase: ProductUseCase) : ViewModel() {

    val productsData = MutableLiveData<ArrayList<Product>>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun getProducts() {
        showProgressbar.value = true
        productUseCase.get(
            viewModelScope, null,
            object :
                UseCaseResponse<ArrayList<Product>> {
                override fun onSuccess(result: ArrayList<Product>) {
                    Log.i(TAG, "result: $result")
                    productsData.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }
            },
        )
    }


    fun addProduct(product: Product) {
        showProgressbar.value = true

        //add locally
        productsData.value?.add(0,product)
        productsData.value = productsData.value
        //add to server
        productUseCase.add(
            viewModelScope, product,
            object :
                UseCaseResponse<JsonElement> {
                override fun onSuccess(result: JsonElement) {
                    Log.i(TAG, "result: $result")
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }
            },
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = ProductViewModel::class.java.name
    }

}