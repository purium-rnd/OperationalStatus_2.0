package com.example.test.serverApi

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "response")
class Response {

    @set:Element(name = "header")
    @get:Element(name = "header")
    var header: Header? = null

    @set:Element(name = "body")
    @get:Element(name = "body")
    var body: Body? =null

    class Header{
        @set:Element(name = "resultCode")
        @get:Element(name = "resultCode")
        var resultCode:String? = null

        @set:Element(name = "resultMsg")
        @get:Element(name = "resultMsg")
        var resultMsg:String? = null
    }

    class Body{
        @set:ElementList(name = "items")
        @get:ElementList(name = "items")
        var items:List<Item>? = null

        @set:Element(name = "numOfRows")
        @get:Element(name = "numOfRows")
        var numOfRows:String? = null

        @set:Element(name = "pageNo")
        @get:Element(name = "pageNo")
        var pageNo:String? = null

        @set:Element(name = "totalCount")
        @get:Element(name = "totalCount")
        var totalCount:String? = null
    }

    class Item{
        @set:Element(name = "dataTime")
        @get:Element(name = "dataTime")
        var dataTime:String? = null

        @set:Element(name = "cityName")
        @get:Element(name = "cityName")
        var cityName:String? = null

        @set:Element(name = "so2Value")
        @get:Element(name = "so2Value")
        var so2Value:String? = null

        @set:Element(name = "coValue")
        @get:Element(name = "coValue")
        var coValue:String? = null

        @set:Element(name = "o3Value")
        @get:Element(name = "o3Value")
        var o3Value:String? = null

        @set:Element(name = "no2Value")
        @get:Element(name = "no2Value")
        var no2Value:String? = null

        @set:Element(name = "pm10Value")
        @get:Element(name = "pm10Value")
        var pm10Value:String? = null

        @set:Element(name = "pm25Value")
        @get:Element(name = "pm25Value")
        var pm25Value:String? = null
    }

}