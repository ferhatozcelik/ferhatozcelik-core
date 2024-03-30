package com.ferhatozcelik.iot.esp8266.services

import retrofit2.Call
import retrofit2.http.GET

interface EspService {
    @GET("/")
    fun getData(): Call<Any>

}