package com.ferhatozcelik.iot.esp8266.services

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EspApi(private val address: String) {

    val service: EspService

    init {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addNetworkInterceptor(logging)
                .build()

        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(address)
                .build()

        service = retrofit.create(EspService::class.java)

    }

}