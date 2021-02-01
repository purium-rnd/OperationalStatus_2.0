package com.example.test.serverApi.device

class TokenData(var common:PlaceData.Common,var body:ResponseBody) {

    class ResponseBody{
        var token:String =""
        var device: DeviceData? = null
    }

    class DeviceData {
        var id:Int = -1
        var place_id = -1
        var type:String = ""
        var name = ""
        var serialno: String = ""
        var awair_id = -1
        var mac_address = ""
        var connect = -1
        var maker = ""
        var floor = ""
        var position = ""
        var expire_date = ""
        var reg_date = ""
        var mod_date = ""
        var place_name = ""
        var company_name = ""
        var awair_name = ""
    }
}