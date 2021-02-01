package com.example.test.serverApi

import retrofit2.Call
import retrofit2.http.*

interface CurPositionForIPService {

    @GET("getCtprvnMesureSidoLIst?serviceKey=OD2%2BtlZgxfskdgqzmT53GlLveUn58CX2m1TP8DIx8E2xLsS8Zh3MElKwcz0OeIh1DBPw%2FReN8Vxtr4x%2F5YUEbA%3D%3D")
    fun getFineDustInfo(
        @Query("numOfRows") rows:Int,
        @Query("pageNo") pageNum:Int,
        @Query("sidoName") sidoName:String,
        @Query("searchCondition") searchCondition:String
    ): Call<Response>
}