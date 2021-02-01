package com.example.test.serverApi.device

import retrofit2.Call
import retrofit2.http.*

interface GetPlaceService {
    @GET
    fun getPlace(
        @Url url:String,
        @Header("channel") device:String,
        @Header("x-access-token") token:String
    ): Call<PlaceData>

}