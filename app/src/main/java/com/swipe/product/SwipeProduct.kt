package com.swipe.product

import android.app.Application
import androidx.multidex.MultiDex
import com.swipe.product.di.AppModule
import com.swipe.product.di.NetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class SwipeProduct : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@SwipeProduct)
            modules(listOf(AppModule, NetworkModule))
        }

    }
}