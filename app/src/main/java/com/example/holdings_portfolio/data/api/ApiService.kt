package com.example.holdings_portfolio.data.api

import com.example.holdings_portfolio.data.model.HoldingsResponse
import retrofit2.http.GET

interface ApiService {
    @GET(".")
    suspend fun getHoldings(): HoldingsResponse
}