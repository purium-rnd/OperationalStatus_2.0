package com.example.test.serverApi

import retrofit2.Call
import retrofit2.http.*

interface GpsInfoService {

    @GET("json")
    fun getGpsInfo(): Call<GpsData>
}