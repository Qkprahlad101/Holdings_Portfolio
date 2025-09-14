package com.example.holdings_portfolio.di

import com.example.holdings_portfolio.data.api.ApiService
import com.example.holdings_portfolio.data.repository.HoldingsRepositoryImpl
import com.example.holdings_portfolio.domain.repository.HoldingsRepository
import com.example.holdings_portfolio.domain.usecase.GetHoldingsUseCase
import com.example.holdings_portfolio.presentation.HoldingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }

    single<HoldingsRepository> { HoldingsRepositoryImpl(get(), get()) }

    factory { GetHoldingsUseCase(get()) }

    viewModel { HoldingsViewModel(get()) }
}