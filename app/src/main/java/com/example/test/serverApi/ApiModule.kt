package com.example.test.serverApi

import android.util.Log
import com.example.test.serverApi.device.GetPlaceService
import com.example.test.serverApi.device.GetTokenService
import com.example.test.serverApi.device.PlaceData
import com.example.test.serverApi.device.TokenData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.net.NetworkInterface
import java.net.URLEncoder
import java.util.*

object ApiModule {
//    https://api.openweathermap.org/data/2.5/weather?lat=37.4690458&lon=126.7073177&appid=934d37af9f133e37125f080fcf5241bc
    //http://api.openweathermap.org/data/2.5/forecast?lat=37.4690458&lon=126.7073177&appid=934d37af9f133e37125f080fcf5241bc

    val GPS_URL = "http://ip-api.com/"
    val BASE_URI = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/"
    val CUR_WEATHER_URL = "https://api.openweathermap.org/"
    var baseRetrofit: Retrofit? = null
    var curWeatherRetrofit: Retrofit? = null
    var forecastWeatherRetrofit: Retrofit? = null
    var gpsRetrofit:Retrofit? = null
    var curPositionForIP: CurPositionForIPService? = null
    var curWeatherInfoService:CurWeatherInfoService? = null
    var forecastInfoService:ForecastInfoService? = null
    var gpsInfoService:GpsInfoService? = null
    val OPEN_WEATHER_API_KEY = "934d37af9f133e37125f080fcf5241bc"
    val PURIUM_SERVER = "http://34.80.34.165:4000/"
    var puriumRetrofit:Retrofit? = null
    var getTokenService: GetTokenService
    var placeService: GetPlaceService

    init{
        var httpClient = OkHttpClient.Builder()
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.level = HttpLoggingInterceptor.Level.BODY
//        httpClient.interceptors()mResouceArray.add(interceptor)

        baseRetrofit = Retrofit.Builder().baseUrl(BASE_URI).addConverterFactory(SimpleXmlConverterFactory.create()).client(httpClient.build()).build()
        curWeatherRetrofit = Retrofit.Builder().baseUrl(CUR_WEATHER_URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build()
        forecastWeatherRetrofit = Retrofit.Builder().baseUrl(CUR_WEATHER_URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build()
        gpsRetrofit = Retrofit.Builder().baseUrl(GPS_URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build()

        puriumRetrofit = Retrofit.Builder().baseUrl(PURIUM_SERVER).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build()

        curWeatherInfoService = curWeatherRetrofit!!.create(CurWeatherInfoService::class.java)
        getTokenService = puriumRetrofit!!.create(GetTokenService::class.java)
        placeService = puriumRetrofit!!.create(GetPlaceService::class.java)

        curPositionForIP = baseRetrofit!!.create(
            CurPositionForIPService::class.java)

        curWeatherInfoService = curWeatherRetrofit!!.create(
            CurWeatherInfoService::class.java
        )

        forecastInfoService = forecastWeatherRetrofit!!.create(
            ForecastInfoService::class.java
        )

        gpsInfoService = gpsRetrofit!!.create(GpsInfoService::class.java)

    }

    fun getGpsInfo():Call<GpsData>{
        return gpsInfoService!!.getGpsInfo()
    }

    fun getCurWeatherDate(lat:String,lon:String): Call<CurWeatherData>{
        return curWeatherInfoService!!.getCurWeatherInfo(lat,lon, OPEN_WEATHER_API_KEY)
    }

    fun getForeCastWeatherData(lat:String,lon:String):Call<ForecastData>{
        return forecastInfoService!!.getForecastInfo(lat, lon, OPEN_WEATHER_API_KEY)
    }

    fun getDustList(sidoName:String,totalNum:Int): Call<Response> {
        Log.i("by_debug","OD2%2BtlZgxfskdgqzmT53GlLveUn58CX2m1TP8DIx8E2xLsS8Zh3MElKwcz0OeIh1DBPw%2FReN8Vxtr4x%2F5YUEbA%3D%3D")
        Log.i("by_debug","${URLEncoder.encode("OD2%2BtlZgxfskdgqzmT53GlLveUn58CX2m1TP8DIx8E2xLsS8Zh3MElKwcz0OeIh1DBPw%2FReN8Vxtr4x%2F5YUEbA%3D%3D","UTF-8")}")
        return curPositionForIP!!.getFineDustInfo(totalNum,
            1,sidoName,"HOUR")
    }

    fun getTotalCount(sidoName:String): Call<Response> {
        Log.i("by_debug","OD2%2BtlZgxfskdgqzmT53GlLveUn58CX2m1TP8DIx8E2xLsS8Zh3MElKwcz0OeIh1DBPw%2FReN8Vxtr4x%2F5YUEbA%3D%3D")
        Log.i("by_debug","${URLEncoder.encode("OD2%2BtlZgxfskdgqzmT53GlLveUn58CX2m1TP8DIx8E2xLsS8Zh3MElKwcz0OeIh1DBPw%2FReN8Vxtr4x%2F5YUEbA%3D%3D","UTF-8")}")
        return curPositionForIP!!.getFineDustInfo(10,
            1,sidoName,"HOUR")
    }

    fun getToken(macAddr:String): Call<TokenData> {
        return getTokenService.getToken("web",macAddr)
    }

    fun getPlace(id:String,token:String):Call<PlaceData>{
        return placeService.getPlace("place/$id","web",token)
    }

    fun getMACAddress(interfaceName:String ):String {
        try {
            var interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (interfaceName != null) {
                    if (!intf.name.equals(interfaceName, true)) continue;
                }
                var mac = intf.getHardwareAddress();
                if (mac == null) return "";

                var buf = StringBuilder();

                for (idx in 0 until mac.size) {
                    buf.append(String.format("%02X:", mac[idx]));
                }

                if (buf.length > 0) buf.deleteCharAt(buf.length - 1);
                return buf.toString();
            }
        } catch (ex: Exception) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "null";
    }
}