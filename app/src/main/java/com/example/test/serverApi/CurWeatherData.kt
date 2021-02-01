package com.example.test.serverApi

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class CurWeatherData(var coord: Coord,var weather:JsonArray,var base:String,var main:Main,var wind:Wind) {

    class Coord{
        var lon:String? = null
        var lat:String? = null
    }

    class Wind{
        var speed:String? =null
        var deg:String? = null
    }

    class Weather{
        var weatherInfoArray:List<WeatherArrayData>? = null
    }

    class WeatherArrayData{
        var id:String? =null
        var main:String? = null
        var description:String? = null
        var icon:String? = null
    }

    class Main{
        var temp:String? = null
        var pressure:String? = null
        var humidity:String? = null
        var temp_min:String? = null
        var temp_max:String? = null
    }
}