package com.example.currency_rate

interface DataFetcher {
    suspend fun fetchDataFromNetwork(): String
}