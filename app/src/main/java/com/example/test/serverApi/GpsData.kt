package com.example.test.serverApi

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class GpsData(var lat:Double, var lon:Double,var regionName:String)