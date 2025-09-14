package com.example.holdings_portfolio

import android.app.Application
import com.example.holdings_portfolio.di.appModule
import com.example.holdings_portfolio.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
        }
    }
}