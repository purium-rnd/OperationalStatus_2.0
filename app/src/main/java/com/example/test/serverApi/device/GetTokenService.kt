package com.example.test.serverApi.device

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GetTokenService {

    @GET("/auth/device/token")
    fun getToken(
        @Header("channel") channel:String,
        @Header("mac_address") macAddr:String
    ): Call<TokenData>
}