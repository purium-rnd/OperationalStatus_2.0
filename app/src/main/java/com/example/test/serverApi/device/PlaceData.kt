package com.example.test.serverApi.device

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class PlaceData(var common:Common, var body:ResponseBody){

    class Common{
        var success:Boolean = false
        var error: Error? = null
        var message:String=""
    }

        class ResponseBody{
            var place:List<Place>? = null
        }

        class Place{
            var id:String = ""
            var company_id:Int = -1
            var name:String = ""
            var type:String = ""
            var business_num:String = ""
            var tel:String = ""
            var post:String = ""
            var addr:String = ""
            var addr_detl:String = ""
            var location:String = ""
            var expire_date:String = ""
            var reg_date:String = ""
            var mod_date:String = ""
            var sido:String =""
            var gugun:String =""
            var latitude = ""
            var longitude = ""
        }

    class Error{
        var code:Int = -1
        var detail:String = ""
    }
}