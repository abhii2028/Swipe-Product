package com.swipe.product.domain.usecase.base

import com.google.gson.JsonElement
import com.swipe.product.domain.exception.traceErrorException
import com.swipe.product.domain.model.Product
import kotlinx.coroutines.*
import java.util.concurrent.CancellationException

abstract class UseCase<in Params> {

    abstract suspend fun get(params: Params? = null):ArrayList<Product>
    abstract suspend fun add(params: Params? = null):JsonElement


    fun get(scope: CoroutineScope, params: Params?, onResult: UseCaseResponse<ArrayList<Product>>) {

        scope.launch {
            try {
                val result = get(params)
                onResult.onSuccess(result)
            } catch (e: CancellationException) {
                e.printStackTrace()
                onResult.onError(traceErrorException(e))
            } catch (e: Exception) {
                e.printStackTrace()
                onResult.onError(traceErrorException(e))
            }
        }
    }

    fun add(scope: CoroutineScope, params: Params?, onResult: UseCaseResponse<JsonElement>) {

        scope.launch {
            try {
                val result = add(params)
                onResult.onSuccess(result)
            } catch (e: CancellationException) {
                e.printStackTrace()
                onResult.onError(traceErrorException(e))
            } catch (e: Exception) {
                e.printStackTrace()
                onResult.onError(traceErrorException(e))
            }
        }
    }

}