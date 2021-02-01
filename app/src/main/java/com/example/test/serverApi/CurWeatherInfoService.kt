package com.example.test.serverApi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurWeatherInfoService {

    @GET("/data/2.5/weather/")
    fun getCurWeatherInfo(
        @Query("lat") lat:String,
        @Query("lon") lon:String,
        @Query("appid") appid:String
        ): Call<CurWeatherData>
}