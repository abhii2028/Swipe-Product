package com.swipe.product.di

import com.swipe.product.presentation.products.ProductViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {

    viewModel { ProductViewModel(get()) }

    single { createProductUseCase(get()) }

    single { createProductRepository(get()) }
}