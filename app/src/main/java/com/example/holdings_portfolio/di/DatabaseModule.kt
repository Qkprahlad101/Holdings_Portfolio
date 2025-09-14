package com.example.holdings_portfolio.di

import androidx.room.Room
import com.example.holdingsapp.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "holdings_database"
        ).build()
    }

    single { get<AppDatabase>().holdingsDao() }
}