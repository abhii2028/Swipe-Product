package com.swipe.product.domain.usecase.base

import com.swipe.product.domain.model.ApiError

interface UseCaseResponse<Type> {

    fun onSuccess(result: Type)

    fun onError(apiError: ApiError?)
}

