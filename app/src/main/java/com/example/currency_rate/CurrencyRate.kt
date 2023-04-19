package com.example.currency_rate

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import java.util.*

// Data classes for XML parsing
@Root(name = "Currency", strict = false)
data class Currency (
    @field:Attribute(name = "ISOCode")
    var isoCode: String = "",

    @field:Element(name = "Value")
    var value: String = ""
)

@Root(name = "CurrencyRates", strict = false)
data class CurrencyRates (
    @field:ElementList(name = "Currency", inline = true)
    var currencyList: List<Currency> = mutableListOf(),

    @field:Attribute(name = "Date", required = false)
    var date: String = ""
)

interface NbkrApiService {
    @GET("/XML/daily.xml")
    suspend fun getCurrencyRates(): CurrencyRates
}

suspend fun fetchCurrencyRates(): CurrencyRates {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.nbkr.kg/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    val nbkrApiService = retrofit.create(NbkrApiService::class.java)
    return nbkrApiService.getCurrencyRates()
}