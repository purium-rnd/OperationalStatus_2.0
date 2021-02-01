package com.example.test

import android.location.Address
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*
import android.view.WindowManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Callback
import java.text.SimpleDateFormat
import android.R.attr.y
import android.R.attr.x
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.RED
import android.graphics.Color.YELLOW
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.net.wifi.WifiManager.ACTION_PICK_WIFI_NETWORK
import android.os.*
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.Display
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.test.serial.UsbService
import com.example.test.serverApi.*
import com.example.test.serverApi.device.PlaceData
import com.example.test.serverApi.device.TokenData
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieEntry
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(),ChangedTimeListener {
    // 2019.09.10 네오카텍 app 에서 보낸 action 값
    val ACTION_DATA = "neocartek.intent.action.DEVICE_DATA"

    var mTimeChangeReciver:TimeChangeReceiver? = null
    var timeIntentFilter: IntentFilter = IntentFilter()
    var mUiHandler: Handler = Handler(Looper.getMainLooper())
    var mSi:String = ""
    var mDo:String = ""
    var mDataBroadcastReceiver:DataBroadcastReceiver? = null
    var dataIntentFilter: IntentFilter = IntentFilter()

    private val PERMISSIONS_ACCESS_FINE_LOCATION = 1000
    private val PERMISSIONS_ACCESS_COARSE_LOCATION = 1001
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val WIFI_ENABLE_REQUEST_CODE = 2002

    private var isAccessFineLocation = false
    private var isAccessCoarseLocation = false
    private var isPermission = false
    private var mInputFiveClickGotoWifiSetting:InputFiveClickGotoWifiSetting = InputFiveClickGotoWifiSetting()

    private var usbService: UsbService? = null
    private var mHandler: MyHandler? = null

    lateinit var mPm10Chart:PieChart
    lateinit var mPm25Chart:PieChart
    var pIntent: PendingIntent? = null

    var isWating:Boolean = true
    var isDemo:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        var visibility = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = visibility.xor(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    xor View.SYSTEM_UI_FLAG_FULLSCREEN
                    xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setContentView(R.layout.activity_main)

        mHandler = MyHandler(this)
        //http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?serviceKey=OD2%2BtlZgxfskdgqzmT53GlLveUn58CX2m1TP8DIx8E2xLsS8Zh3MElKwcz0OeIh1DBPw%2FReN8Vxtr4x%2F5YUEbA%3D%3D&numOfRows=25&pageNo=1&sidoName=%EC%84%9C%EC%9A%B8&searchCondition=HOUR
        //serviceKey =
        //미세먼지 기준 0~30 좋음 , 31~80 보통, 81~150 나쁨, 151~ 매우나쁨
        //초미세먼지 기준 0~15 좋음 16~35 보통 36~75 나쁨 76~ 매우나쁨

        Log.d("by_debug","체감 온도 = ${getWindChillTemp(26.3,1.8)}")

        tv_cur_time.text = getCurrentTime(System.currentTimeMillis())
        tv_cur_date_address.text = getNowFullDate(System.currentTimeMillis())
//        mPm10Chart = pm10_pie_chart
//        mPm25Chart = pm25_pie_chart

        img_logo.setOnClickListener {
            mInputFiveClickGotoWifiSetting.onClickLogoImg(this@MainActivity)
        }
//        loadWeather()
        //callPermission()

        Log.e("by_debug","mac addr = ${ApiModule.getMACAddress("eth0")}")

        var anim = AnimationUtils.loadAnimation(this, R.anim.rotation)
        anim.setInterpolator(this, android.R.anim.accelerate_decelerate_interpolator)

        circle_1.startAnimation(anim)
        circle_2.startAnimation(anim)
        circle_3.startAnimation(anim)
        circle_4.startAnimation(anim)
/*
        val tempData = 18
        if (tempData != null) {
            //val temp:Int = tempData.toInt()
            Log.d("by_debug","air quality temp data $tempData")
            tempViewUpdate(tempData)
        }

        val humiData = 32
        if (humiData != null) {
            //val humi:Int = humiData.toInt()
            Log.d("by_debug","air quality humi data $humiData")
            humiViewUpdate(humiData)
        }

        val vocData = 0
        if (vocData != null) {
            //val voc:Int = vocData.toInt()
            Log.d("by_debug","air quality voc data $vocData")
            vocViewUpdate(vocData)
        }

        val co2Data = 1330
        if (co2Data != null) {
            //val co2:Int = co2Data.toInt()
            Log.d("by_debug","air quality co2 data $co2Data")
            co2ViewUpdate(co2Data)
        }

        val pm25Data = 76
        if (pm25Data != null) {
            //val pm25:Int = pm25Data.toInt()
            Log.d("by_debug","air quality pm25 data $pm25Data")
            pm25ViewUpdate(pm25Data)
        }

        val pm10Data = 85
        if (pm10Data != null) {
            //val pm10:Int = pm10Data.toInt()
            Log.d("by_debug","air quality pm10 data $pm10Data")
            pm10ViewUpdate(pm10Data)
        }*/
    }

    private fun initVideoView(){
        val videoRootPath = "android.resource://$packageName/"
        video_view.setVideoURI(Uri.parse(videoRootPath + R.raw.wait))
        if(!isDemo){
            video_view.setOnCompletionListener({ mp ->
                if(isWating){
                    video_view.start()
                }else{
                    video_view.pause()
                    video_view.setVideoURI(Uri.parse(videoRootPath + R.raw.wait))
                    video_view.seekTo(0)
                    video_view.start()
                }
            })
            video_view.setOnErrorListener({ mp, what, extra ->
                Log.e("by_debug", "video error")
                true
            })
        }else{
            video_view.setOnCompletionListener({ mp ->
                if(isWating){
                    video_view.pause()
                    video_view.setVideoURI(Uri.parse(videoRootPath + R.raw.remove))
                    video_view.seekTo(0)
                    video_view.start()
                }else{
                    video_view.pause()
                    video_view.setVideoURI(Uri.parse(videoRootPath + R.raw.wait))
                    video_view.seekTo(0)
                    video_view.start()
                }
                isWating = !isWating
            })
        }

        video_view.start()
    }

    private fun getKoreanRegionName(region:String):String{
        var name:String = "서울"

        when(region){
            "Seoul" -> name = "서울"
            "Busan" -> name = "부산"
            "Daegu" -> name = "대구"
            "Gwangju" -> name = "광주"
            "Incheon" -> name = "인천"
            "Daejeon" -> name = "대전"
            "Ulsan" -> name = "울산"
            "Gyeonggi-do" -> name = "경기"
            "Gangwon-do" -> name = "강원"
            "Chungcheongbuk-do" -> name = "충북"
            "Chungcheongnam-do" -> name = "충남"
            "Jeollabuk-do" -> name = "전북"
            "Jeollanam-do" -> name = "전남"
            "Gyeongsangbuk-do" -> name = "경북"
            "Gyeongsangnam-do" -> name = "경남"
            "Jeju-do" -> name = "제주"

            "전라남도" -> name = "전남"
            "전라북도" -> name = "전남"
            "경상북도" -> name = "경북"
            "경상남도" -> name = "경남"
        }

        if(name.length > 2){
            name = name.substring(0,1)
        }

        return name
    }

    override fun onResume() {
        super.onResume()
        initVideoView()

        /*
        var macAddr = ""

        if (isDemo) {
            macAddr = "00-14-B0-AB-00-10"
        } else {
            macAddr = ApiModule.getMACAddress("eth0")
        }

        if(macAddr == "null" || macAddr == ""){
            getGPSInfoForIP()
        }else{
            //mac 주소 있다.
            getAddress(macAddr)
        }*/

/*
        ApiModule.getGpsInfo().enqueue(object : Callback<GpsData>{

            override fun onFailure(call: retrofit2.Call<GpsData>, t: Throwable) {
                Log.d("by_debug", "콜백오류:"+t.message)
            }

            override fun onResponse(
                call: retrofit2.Call<GpsData>,
                response: retrofit2.Response<GpsData>) {
                Log.d("by_debug","onResponse!!")
                Log.i("by_debug","${response.body()?.lat}")
                Log.i("by_debug","${response.body()?.lon}")
                Log.i("by_debug","${response.body()?.regionName}")
                onUpdateGpsInfo(response.body()?.lat!!,response.body()?.lon!!,getKoreanRegionName(response.body()?.regionName!!))
            }
        })*/

        /*
        if(!isDemo){
            setFilters()  // Start listening notifications from UsbService
            startService(
                UsbService::class.java,
                usbConnection,
                null
            ) // Start UsbService(if it was not started before) and Bind it
        }*/

        mTimeChangeReciver = TimeChangeReceiver(this@MainActivity)
        timeIntentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(mTimeChangeReciver,timeIntentFilter)


        // 2019.09.10 네오카텍 app 에서 보낸 센서 data 값을 받기 위한 브로드캐스트 등록
        mDataBroadcastReceiver = DataBroadcastReceiver(this@MainActivity)
        dataIntentFilter.addAction(ACTION_DATA)
        registerReceiver(mDataBroadcastReceiver,dataIntentFilter)

        //showVideo()
    }

    fun getWindChillTemp(temp:Double,wind:Double):Int{
        var v3 = Math.pow(wind*3.6, 0.16);
        return (13.12 + 0.6215*temp - (11.37*v3)+0.3965*v3*temp).toInt()
    }

    override fun timeChanged(time: String) {
        Log.d("by_debug","timeChanged!!")
        tv_cur_time.text = time
        tv_cur_date_address.text = getNowFullDate(System.currentTimeMillis())
/*
        var min = time.split(":")[1]
        Log.v("by_debug","min = ${time.split(":")[1]}")
        if(min == "30" || min == "00"){
            var macAddr = ""

            if (isDemo) {
                macAddr = "00-14-B0-AB-00-10"
            } else {
                macAddr = ApiModule.getMACAddress("eth0")
            }

            if(macAddr == "null" || macAddr == ""){
                getGPSInfoForIP()
            }else{
                //mac 주소 있다.
                getAddress(macAddr)
            }
        }*/
        /*
        tv_cur_time.text = time
        tv_cur_date_address.text = getNowFullDate(System.currentTimeMillis())+" / $mDo  $mSi"

        var min = time.split(":")[1]
        Log.v("by_debug","min = ${time.split(":")[1]}")
        if(min == "30" || min == "00"){
//            loadWeather()
            ApiModule.getGpsInfo().enqueue(object : Callback<GpsData>{
                override fun onFailure(call: retrofit2.Call<GpsData>, t: Throwable) {
                    Log.d("by_debug", "콜백오류:"+t.message)
                }

                override fun onResponse(
                    call: retrofit2.Call<GpsData>,
                    response: retrofit2.Response<GpsData>
                ) {
                    Log.d("by_debug","onResponse!!")
                    Log.i("by_debug","${response.body()?.lat}")
                    Log.i("by_debug","${response.body()?.lon}")
                    onUpdateGpsInfo(response.body()?.lat!!,response.body()?.lon!!,getKoreanRegionName(response.body()?.regionName!!))
                }
            })
        }*/
    }

    private fun callPermission():Boolean {

        // Check the SDK version and whether the permission is already granted or not.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSIONS_ACCESS_FINE_LOCATION);
            return false
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(
//                    arrayOf(),
//                    PERMISSIONS_ACCESS_COARSE_LOCATION);
//
//        }
        else {
//            isPermission = true;
//            loadWeather()
            return true
        }
    }
/*
   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        Log.d("by_debug","onRequestPermissionsResult $requestCode")

        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessFineLocation = true
        }

        if (isAccessFineLocation) {
            isPermission = true
//            loadWeather()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }*/
/*
    private fun showDialogForLocationServiceSetting() {

        if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.M){
            ApiModule.getGpsInfo().enqueue(object : Callback<GpsData>{
                override fun onFailure(call: retrofit2.Call<GpsData>, t: Throwable) {
                    Log.d("by_debug", "콜백오류:"+t.message)
                }

                override fun onResponse(
                    call: retrofit2.Call<GpsData>,
                    response: retrofit2.Response<GpsData>
                ) {
                    Log.d("by_debug","onResponse!!")
                    Log.i("by_debug","${response.body()?.lat}")
                    Log.i("by_debug","${response.body()?.lon}")
                    onUpdateGpsInfo(response.body()?.lat!!,response.body()?.lon!!,getKoreanRegionName(response.body()?.regionName!!))
                }
            })
            return
        }

        var builder = AlertDialog.Builder(this@MainActivity);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정",DialogInterface.OnClickListener{
            dialog: DialogInterface?, which: Int ->
            var callGPSSettingIntent
                = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        })

        builder.setNegativeButton("취소", DialogInterface.OnClickListener{
                dialog: DialogInterface?, which: Int ->
            dialog!!.cancel()
        })
        builder.create().show()
    }

//    private fun showDialogForWifiSetting() {
//        var builder = AlertDialog.Builder(this@MainActivity);
//        builder.setTitle("인터넷 비활성화");
//        builder.setMessage("앱을 사용하기 위해서는 인터넷 연결이 필요합니다.\n"
//                + "인터넷 설정을 수정하시겠습니까?");
//        builder.setCancelable(true);
//        builder.setPositiveButton("설정",DialogInterface.OnClickListener{
//                dialog: DialogInterface?, which: Int ->
//            var callGPSSettingIntent
//                    = Intent(ACTION_PICK_WIFI_NETWORK);
//            startActivityForResult(callGPSSettingIntent, WIFI_ENABLE_REQUEST_CODE);
//        })
//
//        builder.setNegativeButton("취소", DialogInterface.OnClickListener{
//                dialog: DialogInterface?, which: Int ->
//            dialog!!.cancel()
//        })
//        builder.create().show()
//    }*/

    override fun onPause() {
        super.onPause()
        if(mTimeChangeReciver != null)
            unregisterReceiver(mTimeChangeReciver)

        if(video_view != null){
            video_view.pause()
        }
//        if(mUsbReceiver != null)
//            unregisterReceiver(mUsbReceiver)
        /*
        if(!isDemo)
            unbindService(usbConnection)*/

        if(mDataBroadcastReceiver != null)
            unregisterReceiver(mDataBroadcastReceiver)
    }

/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->{
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음")
//                        callPermission()
                        return
                }
            }
            WIFI_ENABLE_REQUEST_CODE->{
//                loadWeather()
            }
        }
    }



    public fun checkLocationServicesStatus():Boolean {
        var locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }*/

/*
    fun loadWeather(doName:String,gugun:String,latitude:Double,longitude: Double){
        mDo = doName
        mSi = gugun
        getCurWeatherData(latitude.toString(),longitude.toString())
        getForecastData(latitude.toString(),longitude.toString())
        getTomorrowDate()
        getTotalCount(doName,gugun)

        tv_cur_time.text = getCurrentTime(System.currentTimeMillis())
        tv_cur_date_address.text = getNowFullDate(System.currentTimeMillis())+" / $mDo  $mSi"
    }
    fun loadWeatherForIp(lat:Double,lon:Double,region: String){

        val latitude = lat
        val longitude = lon

        mDo = region
        getCurWeatherData(latitude.toString(),longitude.toString())
        getForecastData(latitude.toString(),longitude.toString())
        getTotalCount(region,"")
        tv_cur_time.text = getCurrentTime(System.currentTimeMillis())
        tv_cur_date_address.text = getNowFullDate(System.currentTimeMillis())+" / $mDo"
    }
    fun getAddress(macAddr:String){
        ApiModule.getToken(macAddr).enqueue(object : Callback<TokenData> {
            override fun onFailure(call: retrofit2.Call<TokenData>, t: Throwable) {
                Log.d("by_debug", "콜백오류:" + t.message)
//                mLoadingDialog?.setCurStateText(mDialogStrings[alertNetwordError])
//                mLoadingDialog?.dismiss()
            }

            override fun onResponse(
                call: retrofit2.Call<TokenData>,
                response: retrofit2.Response<TokenData>
            ) {
                Log.d("by_debug", "device info body = ${response.body()}")
                if(response.body() != null){
                    Log.d("by_debug", "device info common = ${response.body()?.common}")
                    Log.d("by_debug", "device info body body = ${response.body()?.body}")
                    Log.d("by_debug", "device info place id = ${response.body()?.body?.device?.place_id}")

                    if(!response.body()!!.common.success){
                        Log.d("by_debug", "common.success")
                        getGPSInfoForIP()
                        return
                    }

                    ApiModule.getPlace(response.body()!!.body.device!!.place_id.toString(),response.body()!!.body.token).enqueue(object : Callback<PlaceData> {
                        override fun onFailure(call: retrofit2.Call<PlaceData>, t: Throwable) {
                            Log.d("by_debug", "콜백오류:" + t.message)
                        }

                        override fun onResponse(
                            call: Call<PlaceData>,
                            response: retrofit2.Response<PlaceData>
                        ) {

                            var isAddrNull = false

                            if(response.body() != null){
                                if(!response.body()!!.body.place.isNullOrEmpty()){
                                    Log.d("by_debug", "place info sido = ${response.body()!!.body.place!![0].sido}")
                                    Log.d("by_debug", "place info gugun = ${response.body()!!.body.place!![0].gugun}")
                                    Log.d("by_debug", "place info lat = ${response.body()!!.body.place!![0].latitude}")
                                    Log.d("by_debug", "place info lon = ${response.body()!!.body.place!![0].longitude}")
                                    Log.d("by_debug", "place info korean region name = ${getKoreanRegionName(response.body()!!.body.place!![0].sido)}")
                                    loadWeather(getKoreanRegionName(response.body()!!.body.place!![0].sido),
                                        response.body()!!.body.place!![0].gugun,
                                        response.body()!!.body.place!![0].latitude.toDouble(),
                                        response.body()!!.body.place!![0].longitude.toDouble())
                                }else{
                                    isAddrNull = true
                                }
                            }else{
                                isAddrNull = true
                            }

                            if(isAddrNull){
                                getGPSInfoForIP()
                            }
                        }
                    })
                }else{
                    getGPSInfoForIP()
                }
            }
        })
    }

    fun getGPSInfoForIP(){
        ApiModule.getGpsInfo().enqueue(object : Callback<GpsData>{
            override fun onFailure(call: retrofit2.Call<GpsData>, t: Throwable) {
                Log.d("by_debug", "콜백오류:"+t.message)
            }

            override fun onResponse(
                call: retrofit2.Call<GpsData>,
                response: retrofit2.Response<GpsData>) {
                Log.d("by_debug","onResponse!!")
                Log.i("by_debug","${response.body()?.lat}")
                Log.i("by_debug","${response.body()?.lon}")
                Log.i("by_debug","${response.body()?.regionName}")
                onUpdateGpsInfo(response.body()?.lat!!,response.body()?.lon!!,getKoreanRegionName(response.body()?.regionName!!))
            }
        })
    }*/
/*
    fun loadWeather(){
        var gpsTracker = GpsTracker()
        gpsTracker.getLocation(this@MainActivity)
//            startService(Intent(this@MainActivity,GpsTracker::class.java))

        val latitude = gpsTracker.getLatitude()
        val longitude = gpsTracker.getLongitude()

        Toast.makeText(this@MainActivity,"latitude = "+latitude+" longitude = "+longitude,Toast.LENGTH_SHORT).show()

        if(latitude <= 0 || longitude <= 0){
            loadWeather()
            return
        }

        val address = getCurrentAddress(latitude, longitude)

        Log.d("by_debug","$address")
        Toast.makeText(this@MainActivity, "$address , lat = $latitude , lon = $longitude", Toast.LENGTH_LONG).show()
        Log.d("by_debug","lat = $latitude , lon = $longitude")
        var addressArray = address.split(" ")
        var doName = addressArray[1].substring(0,2)
        var guOrSiName = addressArray[2]
        Log.d("by_debug","$doName")
        Log.d("by_debug","$guOrSiName")
        mDo = doName
        mSi = guOrSiName
        getCurWeatherData(latitude.toString(),longitude.toString())
        getForecastData(latitude.toString(),longitude.toString())
        getTomorrowDate()
        getTotalCount(doName,guOrSiName)

        tv_cur_time.text = getCurrentTime(System.currentTimeMillis())
        tv_cur_date_address.text = getNowFullDate(System.currentTimeMillis())+" / $mDo  $mSi"
    }*/
/*
    fun loadWeatherForIp(lat:Double,lon:Double,region: String){
//        var gpsTracker = GpsTracker()
//        gpsTracker.getLocation(this@MainActivity)
//            startService(Intent(this@MainActivity,GpsTracker::class.java))

        val latitude = lat
        val longitude = lon

//        val address = getCurrentAddress(latitude, longitude)
//        Toast.makeText(this@MainActivity, "cur address = ${getCurrentAddress(latitude, longitude)}", Toast.LENGTH_LONG).show()

//        Log.d("by_debug","$address")
//        mUiHandler.post {
//            Toast.makeText(this@MainActivity, "$address , lat = $latitude , lon = $longitude", Toast.LENGTH_LONG).show()
//        }
//        Log.d("by_debug","lat = $latitude , lon = $longitude")
//        var addressArray = address.split(" ")
//        var doName = addressArray[1].substring(0,2)
//        var guOrSiName = addressArray[2]
//        Log.d("by_debug","$doName")
//        Log.d("by_debug","$guOrSiName")
        mDo = region
//        mSi = guOrSiName
        getCurWeatherData(latitude.toString(),longitude.toString())
        getForecastData(latitude.toString(),longitude.toString())
//        getTomorrowDate()
        getTotalCount(region,"")
//
        tv_cur_time.text = getCurrentTime(System.currentTimeMillis())
        tv_cur_date_address.text = getNowFullDate(System.currentTimeMillis())+" / $mDo"
    }*/

/*
    fun onUpdateServerData(sidoInfo:SidoInfo){
        getFineDustList(sidoInfo)
    }

    fun onUpdateGpsInfo(lat:Double,lon:Double,region:String){
        loadWeatherForIp(lat,lon,region)
    }

    private fun getTotalCount(doName:String,guOrSiName: String){
        ApiModule.getTotalCount(doName).enqueue(object : Callback<Response> {
            override fun onFailure(call: retrofit2.Call<Response>, t: Throwable) {
                Log.d("by_debug", "콜백오류:"+t.message)
            }

            override fun onResponse(
                call: retrofit2.Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                Log.d("by_debug","onResponse!!")
                Log.i("by_debug","${response.body()?.body?.numOfRows}")
                Log.i("by_debug","${response.body()?.body?.pageNo}")
                Log.i("by_debug","${response.body()?.body?.totalCount}")
                Log.i("by_debug","${response.body()?.body?.items?.size}")

                var totalCount = response.body()?.body?.totalCount?.toInt()!!

                onUpdateServerData(SidoInfo(doName,guOrSiName,totalCount))
            }
        })
    }



    private fun getCurWeatherData(lat:String,lon:String){
        ApiModule.getCurWeatherDate(lat,lon).enqueue(object : Callback<CurWeatherData> {
            override fun onFailure(call: retrofit2.Call<CurWeatherData>, t: Throwable) {
                Log.d("by_debug", "콜백오류:"+t.message)
            }

            override fun onResponse(
                call: retrofit2.Call<CurWeatherData>,
                response: retrofit2.Response<CurWeatherData>
            ) {
                Log.d("by_debug","onResponse!!")

                for(i in 0 until response.body()?.weather?.size()!!){
                    var array = response.body()?.weather!!.get(i)
                    Log.d("by_debug","weather = ${array.asJsonObject.get("icon")}")
                }
                Log.d("by_debug","temp = ${(response.body()?.main?.temp!!.toFloat() - 273.15f).toInt()}")
                Log.d("by_debug","tempMin = ${(response.body()?.main?.temp_min!!.toFloat() - 273.15f).toInt()}")
                Log.d("by_debug","tempMax = ${(response.body()?.main?.temp_max!!.toFloat() - 273.15f).toInt()}")
                Log.d("by_debug","wind = ${response.body()?.wind?.speed!!}")
                Log.d("by_debug","chill temp = ${getWindChillTemp(response.body()?.main?.temp!!.toDouble() - 273.15f,response.body()?.wind?.speed!!.toDouble())}")

//                01d 맑음
//                02d 구름 조금
//                03d 흐림
//                04d 매우흐림
//                09d 소나기
//                10d 비
//                11d 천둥번개
//                13d 눈
//                50d 안개
                mUiHandler.post {
                    tv_cur_temp.text = ((response.body()?.main?.temp!!.toFloat() - 273.15f).toInt()).toString()
                    tv_cur_weather.text = getWeatherText(response.body()?.weather!!.get(0).asJsonObject.get("icon").asString)
                    img_today_weather.background = getWeatherIcon(response.body()?.weather!!.get(0).asJsonObject.get("icon").asString,false)
                    today_temp_low_high.text = "${(response.body()?.main?.temp_min!!.toFloat() - 273.15f).toInt()}/${(response.body()?.main?.temp_max!!.toFloat() - 273.15f).toInt()}"
                    today_feel_temp.text = "${getWindChillTemp(response.body()?.main?.temp!!.toDouble() - 273.15f,response.body()?.wind?.speed!!.toDouble())}"

                    Log.w("by_debug","weather = ${getWeatherText(response.body()?.weather!!.get(0).asJsonObject.get("icon").toString())}")
                    Log.w("by_debug","weather = ${response.body()?.weather!!.get(0).asJsonObject.get("icon").toString()}")
                }
            }
        })
    }

    private fun getWeatherIcon(icon:String,isNight:Boolean):Drawable{
        var drawable:Drawable? = getDrawable(R.drawable.weather_01_d)
        when(icon){
            "01d" ->{drawable = getDrawable(R.drawable.weather_01_d)}
            "02d" ->{drawable = getDrawable(R.drawable.weather_02_d)}
            "03d" ->{drawable = getDrawable(R.drawable.weather_03_d)}
            "04d" ->{drawable = getDrawable(R.drawable.weather_04_d)}
            "09d" ->{drawable = getDrawable(R.drawable.weather_09_d)}
            "10d" ->{drawable = getDrawable(R.drawable.weather_10_d)}
            "11d" ->{drawable = getDrawable(R.drawable.weather_11_d)}
            "13d" ->{drawable = getDrawable(R.drawable.weather_13_d)}
            "50d" ->{drawable = getDrawable(R.drawable.weather_50_d)}

            "01n" ->{drawable = getDrawable(R.drawable.weather_01_n)}
            "02n" ->{drawable = getDrawable(R.drawable.weather_02_n)}
            "03n" ->{drawable = getDrawable(R.drawable.weather_03_n)}
            "04n" ->{drawable = getDrawable(R.drawable.weather_04_n)}
            "09n" ->{drawable = getDrawable(R.drawable.weather_09_n)}
            "10n" ->{drawable = getDrawable(R.drawable.weather_10_n)}
            "11n" ->{drawable = getDrawable(R.drawable.weather_11_n)}
            "13n" ->{drawable = getDrawable(R.drawable.weather_13_n)}
            "50n" ->{drawable = getDrawable(R.drawable.weather_50_n)}
        }

        return drawable!!
    }

    private fun getWeatherText(icon:String):String{

        var weatherText:String = ""

        when(icon){
            "01d" ->{weatherText = "맑음"}
            "02d" ->{weatherText = "구름조금"}
            "03d" ->{weatherText = "흐림"}
            "04d" ->{weatherText = "매우흐림"}
            "09d" ->{weatherText = "비"}
            "10d" ->{weatherText = "비"}
            "11d" ->{weatherText = "천둥번개"}
            "13d" ->{weatherText = "눈"}
            "50d" ->{weatherText = "안개"}
            "01n" ->{weatherText = "맑음"}
            "02n" ->{weatherText = "구름조금"}
            "03n" ->{weatherText = "흐림"}
            "04n" ->{weatherText = "매우흐림"}
            "09n" ->{weatherText = "비"}
            "10n" ->{weatherText = "비"}
            "11n" ->{weatherText = "천둥번개"}
            "13n" ->{weatherText = "눈"}
            "50n" ->{weatherText = "안개"}
        }

        return weatherText
    }

    private fun getForecastData(lat:String,lon:String){
        ApiModule.getForeCastWeatherData(lat,lon).enqueue(object : Callback<ForecastData> {
            override fun onFailure(call: retrofit2.Call<ForecastData>, t: Throwable) {
                Log.d("by_debug", "콜백오류:"+t.message)
            }

            override fun onResponse(
                call: retrofit2.Call<ForecastData>,
                response: retrofit2.Response<ForecastData>
            ) {
                Log.d("by_debug","onResponse!!")

                var tomorrowData = getTomorrowDate()

                var tempMin:Float = 100f
                var tempMax:Float = -100f

                for(i in 0 until response.body()?.list!!.size()){
                    var data = getNowDate(response.body()?.list?.get(i)!!.asJsonObject.getAsJsonPrimitive("dt").asLong * 1000)
                    if(data.contains(tomorrowData)) {
                        Log.e("by_debug","-------------------------------------------------------------------------------")
                        Log.d("by_debug","temp min = ${response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp_min").asFloat - 273.15f}")
                        if(tempMin > (response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp_min").asFloat - 273.15f)){
                            tempMin = response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp_min").asFloat - 273.15f
                        }
                        if(tempMax < (response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp_max").asFloat - 273.15f)){
                            tempMax = response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp_max").asFloat - 273.15f
                        }

                        Log.d("by_debug","temp max = ${response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp_max").asFloat - 273.15f}")
                        Log.d("by_debug","weather = ${response.body()?.list?.get(i)!!.asJsonObject.getAsJsonArray("weather").get(0).asJsonObject.get("main").asString}")
                        Log.d("by_debug","wind = ${response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("wind").get("speed").asDouble}")
                        Log.e("by_debug","-------------------------------------------------------------------------------")
                        if(data.contains("15:00:00")){
                            Log.i("by_debug","date = $data")
                            Log.i("by_debug","temp = ${response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp").asFloat - 273.15f}")
                            Log.i("by_debug","weather = ${response.body()?.list?.get(i)!!.asJsonObject.getAsJsonArray("weather").get(0).asJsonObject.get("main").asString}")
                            Log.i("by_debug","wind = ${response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("wind").get("speed").asDouble}")
                            Log.d("by_debug","내일 체감 = ${getWindChillTemp(response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp").asDouble - 273.15f,
                                                                        response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("wind").get("speed").asDouble)}")

                            mUiHandler.post {
                                tv_tomorrow_temp.text = (response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp").asFloat - 273.15f).toInt().toString()
                                tv_tomorrow_weather.text = getWeatherText(response.body()?.list?.get(i)!!.asJsonObject.getAsJsonArray("weather").get(0).asJsonObject.get("icon").asString)
                                img_tomorrow_weather.background = getWeatherIcon(response.body()?.list?.get(i)!!.asJsonObject.getAsJsonArray("weather").get(0).asJsonObject.get("icon").asString,false)
                                tomorrow_feel_temp.text = "${getWindChillTemp(response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("main").get("temp").asDouble - 273.15f,
                                    response.body()?.list?.get(i)!!.asJsonObject.getAsJsonObject("wind").get("speed").asDouble)}"
                            }
                        }
                    }
                }

                mUiHandler.post {
                    tomorrow_temp_low_high.text = "${tempMin.toInt()}/${tempMax.toInt()}"
                }
                Log.e("by_debug","내일 최저 = ${tempMin.toInt()}")
                Log.e("by_debug","내일 최고 = ${tempMax.toInt()}")
            }
        })
    }

    private fun getTomorrowDate():String{
        val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
        Log.d("by_debug","tomorrow date = ${yearMonthDayFormat.format(Date(System.currentTimeMillis()+86400000L))}")
        return yearMonthDayFormat.format(Date(System.currentTimeMillis()+86400000L))
    }

    private fun getNowDate(time:Long):String{
        val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return yearMonthDayFormat.format(Date(time))
    }*/

    private fun getNowFullDate(time:Long):String{
        val yearMonthDayFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
        //val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd")
        return yearMonthDayFormat.format(Date(time))
    }
/*
    private fun getFineDustList(sidoInfo: SidoInfo){
        ApiModule.getDustList(sidoInfo.sidoName,sidoInfo.totalCount).enqueue(object : Callback<Response> {
            override fun onFailure(call: retrofit2.Call<Response>, t: Throwable) {
                Log.d("by_debug", "콜백오류:"+t.message)
            }

            override fun onResponse(
                call: retrofit2.Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                Log.d("by_debug","onResponse!!")

                var items = response.body()?.body?.items!!

                var i:Int = 0

                Log.d("by_debug","${items[i].cityName}")
                Log.d("by_debug","미세먼지 = ${items[i].pm10Value}")
                Log.d("by_debug","초미세먼지 = ${items[i].pm25Value}")
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 1;
                }
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 2;
                }
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 3;
                }
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 4;
                }
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 5;
                }
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 6;
                }
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 7;
                }
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 8;
                }
                if(items[i].pm10Value!!.toString() == "-" || items[i].pm25Value!!.toString() == "-") {
                    i = 9;
                }
                Log.d("by_debug","${items[i].cityName}")
                Log.d("by_debug","미세먼지 = ${items[i].pm10Value}")
                Log.d("by_debug","초미세먼지 = ${items[i].pm25Value}")

                mUiHandler.post{
                    //미세먼지 기준 0~30 좋음 , 31~80 보통, 81~150 나쁨, 151~ 매우나쁨
                    //초미세먼지 기준 0~15 좋음 16~35 보통 36~75 나쁨 76~ 매우나쁨
                    if(items[i].pm10Value!!.toInt() < 30){
                        tv_pm10_level.text = "좋음"
                        tv_pm10_level.setTextColor(Color.parseColor("#88abda"))
                    }else if(IntRange(30, 80).contains(items[i].pm10Value!!.toInt())){
                        tv_pm10_level.setTextColor(Color.parseColor("#89c997"))
                        tv_pm10_level.text = "보통"
                    }else if(IntRange(81, 150).contains(items[i].pm10Value!!.toInt())){
                        tv_pm10_level.setTextColor(Color.parseColor("#f8b551"))
                        tv_pm10_level.text = "나쁨"
                    }else if(items[i].pm10Value!!.toInt() > 150){
                        tv_pm10_level.setTextColor(Color.parseColor("#d76a7c"))
                        tv_pm10_level.text = "매우나쁨"
                    }

                    if(items[i].pm25Value!!.toInt() < 16){
                        tv_pm25_level.text = "좋음"
                        tv_pm25_level.setTextColor(Color.parseColor("#88abda"))
                    }else if(IntRange(16, 35).contains(items[i].pm25Value!!.toInt())){
                        tv_pm25_level.text = "보통"
                        tv_pm25_level.setTextColor(Color.parseColor("#89c997"))
                    }else if(IntRange(36, 75).contains(items[i].pm25Value!!.toInt())){
                        tv_pm25_level.text = "나쁨"
                        tv_pm25_level.setTextColor(Color.parseColor("#f8b551"))
                    }else if(items[i].pm25Value!!.toInt() > 75){
                        tv_pm25_level.text = "매우나쁨"
                        tv_pm25_level.setTextColor(Color.parseColor("#d76a7c"))
                    }

                    initCircleChart(items[i].pm10Value!!.toInt(),items[i].pm25Value!!.toInt())
                }

//                for(i in 0 until items?.size!!){
//
//                    }else{
//                    }
//                }
            }
        })
    }*/


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    fun getCurrentAddress(latitude: Double, longitude: Double): String {
        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(this@MainActivity, Locale.KOREA)
        val addresses: List<Address>?

        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
            Toast.makeText(this, "$addresses", Toast.LENGTH_LONG).show()
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }

        if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견 ${Geocoder.isPresent()}", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }

        val address = addresses[0]
        return address.getAddressLine(0).toString() + "\n"
    }

    data class SidoInfo(var sidoName:String,var guOrSiName: String,var totalCount:Int)


    private val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.KOREAN)

    fun getCurrentTime(time:Long):String{
        return timeFormat.format(Date(time))
    }


    class TimeChangeReceiver : BroadcastReceiver{
        private val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.KOREAN)
        var changedListener:ChangedTimeListener? = null

        constructor(listener:ChangedTimeListener){
            changedListener = listener
        }

        fun getCurrentTime(time:Long):String{
            return timeFormat.format(Date(time))
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            var action = intent!!.action

            when(action) {
                Intent.ACTION_TIME_TICK -> {
                    Log.d("by_debug","ACTION_TIME_TICK!!")
                    changedListener!!.timeChanged(getCurrentTime(System.currentTimeMillis()))
                }
            }
        }
    }
/*
    fun initCircleChart(pm10Val:Int,pm25Val:Int){
        var pm10Color:Int = Color.parseColor("#88abda")
        var pm25Color:Int = Color.parseColor("#88abda")

        if(pm10Val < 30){
            pm10Color = Color.parseColor("#88abda")
            iv_pm10_icon.background = getDrawable(R.drawable.fine_dust_icon_good)
        }else if(IntRange(30, 80).contains(pm10Val)){
            pm10Color = Color.parseColor("#89c997")
            iv_pm10_icon.background = getDrawable(R.drawable.fine_dust_icon_normal)
        }else if(IntRange(81, 150).contains(pm10Val)){
            pm10Color = Color.parseColor("#f8b551")
            iv_pm10_icon.background = getDrawable(R.drawable.fine_dust_icon_bad)
        }else if(pm10Val > 150){
            pm10Color = Color.parseColor("#d76a7c")
            iv_pm10_icon.background = getDrawable(R.drawable.fine_dust_icon_v_bad)
        }

        if(pm25Val < 16){
            pm25Color = Color.parseColor("#88abda")
            iv_pm25_icon.background = getDrawable(R.drawable.pm_icon_good)
        }else if(IntRange(16, 35).contains(pm25Val)){
            pm25Color = Color.parseColor("#89c997")
            iv_pm25_icon.background = getDrawable(R.drawable.pm_icon_normal)
        }else if(IntRange(36, 75).contains(pm25Val)){
            pm25Color = Color.parseColor("#f8b551")
            iv_pm25_icon.background = getDrawable(R.drawable.pm_icon_bad)
        }else if(pm25Val > 75){
            pm25Color = Color.parseColor("#d76a7c")
            iv_pm25_icon.background = getDrawable(R.drawable.pm_icon_v_bad)
        }


        var colors:MutableList<Int> = mutableListOf(pm10Color,Color.parseColor("#262829"))

        //미세먼지 기준 0~30 좋음 , 31~80 보통, 81~150 나쁨, 151~ 매우나쁨
        //초미세먼지 기준 0~15 좋음 16~35 보통 36~75 나쁨 76~ 매우나쁨

        var pm10 = (pm10Val.toFloat()/151f)*100
        var pm25 = (pm25Val.toFloat()/76f)*100

        Log.v("by_debug","pm10 = $pm10 , val = $pm10Val")
        Log.v("by_debug","pm25 = $pm25 , val = $pm25Val")

        mPm10Chart.setUsePercentValues(true)
        mPm10Chart.description.isEnabled = false
        mPm10Chart.setExtraOffsets(-17f, -17f, -17f, -17f)
        mPm10Chart.legend.isEnabled = false

        mPm10Chart.setTouchEnabled(false)
        mPm10Chart.setTransparentCircleColor(Color.parseColor("#00000000"))
        mPm10Chart.setTransparentCircleAlpha(0)
        mPm10Chart.transparentCircleRadius = 0f
        mPm10Chart.isDrawHoleEnabled = true
        mPm10Chart.holeRadius = 87f
        mPm10Chart.setHoleColor(android.R.color.transparent)

        val yValues = ArrayList<PieEntry>()

        yValues.add(PieEntry(pm10, ""))
        yValues.add(PieEntry(100f-pm10, ""))

        mPm10Chart.animateY(1000, Easing.EasingOption.EaseInOutBack) //애니메이션

        val dataSet = PieDataSet(yValues, "")
        dataSet.colors = colors
        dataSet.valueTextColor = Color.parseColor("#00000000")

        val data = PieData(dataSet)

        mPm10Chart.data = data


        var colors2:MutableList<Int> = mutableListOf(pm25Color,Color.parseColor("#262829"))

        mPm25Chart.setUsePercentValues(true)
        mPm25Chart.description.isEnabled = false
        mPm25Chart.setExtraOffsets(-17f, -17f, -17f, -17f)
        mPm25Chart.legend.isEnabled = false

        mPm25Chart.setTouchEnabled(false)
        mPm25Chart.setTransparentCircleColor(android.R.color.transparent)
        mPm25Chart.setTransparentCircleAlpha(0)
        mPm25Chart.transparentCircleRadius = 0f
        mPm25Chart.isDrawHoleEnabled = true
        mPm25Chart.holeRadius = 87f
        mPm25Chart.setHoleColor(android.R.color.transparent)

        val yValues2 = ArrayList<PieEntry>()

        yValues2.add(PieEntry(pm25, ""))
        yValues2.add(PieEntry(100-pm25, ""))

        mPm25Chart.animateY(1000, Easing.EasingOption.EaseInOutBack) //애니메이션

        val dataSet2 = PieDataSet(yValues2, "")
        dataSet2.colors = colors2
        dataSet2.valueTextColor = Color.parseColor("#00000000")

        val data2 = PieData(dataSet2)

        mPm25Chart.data = data2
    }

    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbService.ACTION_USB_PERMISSION_GRANTED // USB PERMISSION GRANTED
                -> Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show()
                UsbService.ACTION_USB_PERMISSION_NOT_GRANTED // USB PERMISSION NOT GRANTED
                -> Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show()
                UsbService.ACTION_NO_USB // NO USB CONNECTED
                -> Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show()
                UsbService.ACTION_USB_DISCONNECTED // USB DISCONNECTED
                -> Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show()
                UsbService.ACTION_USB_NOT_SUPPORTED // USB NOT SUPPORTED
                -> Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val usbConnection = object : ServiceConnection {
        override fun onServiceConnected(arg0: ComponentName, arg1: IBinder) {
            usbService = (arg1 as UsbService.UsbBinder).service
            usbService!!.setHandler(mHandler)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbService = null
        }
    }

    private fun startService(service: Class<*>, serviceConnection: ServiceConnection, extras: Bundle?) {
        if (!UsbService.SERVICE_CONNECTED) {
            val startService = Intent(this, service)
            if (extras != null && !extras.isEmpty) {
                val keys = extras.keySet()
                for (key in keys) {
                    val extra = extras.getString(key)
                    startService.putExtra(key, extra)
                }
            }
            startService(startService)
        }
        val bindingIntent = Intent(this, service)
        bindService(bindingIntent, usbConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setFilters() {
        val filter = IntentFilter()
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED)
        filter.addAction(UsbService.ACTION_NO_USB)
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED)
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED)
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)
//        registerReceiver(mUsbReceiver, filter)
    }*/

    private fun startVideo(msg:String){
        val videoRootPath = "android.resource://$packageName/"
        if(msg == "F"){
            if(isWating){
                video_view.pause()
                video_view.setVideoURI(Uri.parse(videoRootPath + R.raw.remove))
                video_view.seekTo(0)
                video_view.start()
            }else{
                video_view.pause()
                video_view.setVideoURI(Uri.parse(videoRootPath + R.raw.wait))
                video_view.seekTo(0)
                video_view.start()
            }

            isWating = !isWating
        }
    }

    private class MyHandler(activity: MainActivity) : Handler() {
        private val mActivity: MainActivity
        public var isRunningVideo = false
        init {
            mActivity = activity
        }

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UsbService.MESSAGE_FROM_SERIAL_PORT -> {
                    val data = msg.obj as String
                    Log.d("by_debug","data = $data")
                    if(data == "F"){
                        if(!isRunningVideo){
                            isRunningVideo = true
                            //  mActivity.startVideo(data)
                            mActivity.removeVideo()
                        }
                    }else{

                    }
                }
//                UsbService.CTS_CHANGE -> Toast.makeText(mActivity, "CTS_CHANGE", Toast.LENGTH_LONG).show()
//                UsbService.DSR_CHANGE -> Toast.makeText(mActivity, "DSR_CHANGE", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun waitVideo() {
        val videoRootPath = "android.resource://$packageName/"
        video_view.pause()
        video_view.setVideoURI(Uri.parse(videoRootPath + R.raw.wait))
        video_view.seekTo(0)
        video_view.start()
        video_view.setOnErrorListener({ mp, what, extra ->
            Log.e("by_debug", "wait video error")
            true
        })

        /*Handler().postDelayed({
            removeVideo()
        }, 10000)*/
    }

    private fun removeVideo() {
        val videoRootPath = "android.resource://$packageName/"
        video_view.pause()
        video_view.setVideoURI(Uri.parse(videoRootPath + R.raw.remove))
        video_view.seekTo(0)
        video_view.start()
        video_view.setOnCompletionListener({ mp ->
            mDataBroadcastReceiver!!.isRunningVideo = false
            waitVideo()
        })
        video_view.setOnErrorListener({ mp, what, extra ->
            Log.e("by_debug", "remove video error")
            true
        })
        /*
        video_view.setOnCompletionListener({ mp ->
            mHandler!!.isRunningVideo = false
            waitVideo()
        })*/

        /*
        Handler().postDelayed({
            waitVideo()
        }, 8000)*/
    }
    private fun showVideo() {
        waitVideo()
    }

    private fun timeSetting(date: String) {
        //val timeF = SimpleDateFormat("HH:mm", Locale.KOREAN).parse(date)
        val dateF = SimpleDateFormat("yyyy-MM-dd'T' HH:mm:ss'Z'", Locale.KOREAN).parse(date)

        //tv_cur_time.text = timeF.toString()
        tv_cur_date_address.text = dateF.toString()
    }

    private fun pm10ViewUpdate(data: Int) {
        if (data < 16) {
            var pm_graph_point:Drawable? = getDrawable(R.drawable.pm_graph_point_good)
            pm10_graph_point.background = pm_graph_point

            var pm_level_text:Drawable? = getDrawable(R.drawable.good_text)
            pm10_level_text_img.background = pm_level_text

            var pm_unit:Drawable? = getDrawable(R.drawable.unit_good_ic)
            pm10_unit_img.background = pm_unit

            pm10_point_text.setTextColor(Color.parseColor("#2ec5ff"))
        } else if (data > 15 && data < 36) {
            var pm_graph_point:Drawable? = getDrawable(R.drawable.pm_graph_point_normal)
            pm10_graph_point.background = pm_graph_point

            var pm_level_text:Drawable? = getDrawable(R.drawable.normal_text)
            pm10_level_text_img.background = pm_level_text

            var pm_unit:Drawable? = getDrawable(R.drawable.unit_normal_ic)
            pm10_unit_img.background = pm_unit

            pm10_point_text.setTextColor(Color.parseColor("#00d6bc"))
        } else if (data > 35 && data < 76) {
            var pm_graph_point:Drawable? = getDrawable(R.drawable.pm_graph_point_bad)
            pm10_graph_point.background = pm_graph_point

            var pm_level_text:Drawable? = getDrawable(R.drawable.bad_text)
            pm10_level_text_img.background = pm_level_text

            var pm_unit:Drawable? = getDrawable(R.drawable.unit_bad_ic)
            pm10_unit_img.background = pm_unit

            pm10_point_text.setTextColor(Color.parseColor("#ffbb00"))
        } else if (data > 75) {
            var pm_graph_point:Drawable? = getDrawable(R.drawable.pm_graph_point_verybad)
            pm10_graph_point.background = pm_graph_point

            var pm_level_text:Drawable? = getDrawable(R.drawable.verybad_text)
            pm10_level_text_img.background = pm_level_text

            var pm_unit:Drawable? = getDrawable(R.drawable.unit_verybad_ic)
            pm10_unit_img.background = pm_unit

            pm10_point_text.setTextColor(Color.parseColor("#f15f86"))
        }


        var d:Int = data
        if (data > 99) {
            d = 99
        }
        var rotation = (d * 3.6).toFloat()
        pm10_graph_point.rotation = rotation

        pm10_point_text.setText(d.toString())
    }

    private fun pm25ViewUpdate(data: Int) {
        if (data < 16) {
            var pm_graph_point:Drawable? = getDrawable(R.drawable.pm_graph_point_good)
            pm25_graph_point.background = pm_graph_point

            var pm_level_text:Drawable? = getDrawable(R.drawable.good_text)
            pm25_level_text_img.background = pm_level_text

            var pm_unit:Drawable? = getDrawable(R.drawable.unit_good_ic)
            pm25_unit_img.background = pm_unit

            pm25_point_text.setTextColor(Color.parseColor("#2ec5ff"))
        } else if (data > 15 && data < 36) {
            var pm_graph_point:Drawable? = getDrawable(R.drawable.pm_graph_point_normal)
            pm25_graph_point.background = pm_graph_point

            var pm_level_text:Drawable? = getDrawable(R.drawable.normal_text)
            pm25_level_text_img.background = pm_level_text

            var pm_unit:Drawable? = getDrawable(R.drawable.unit_normal_ic)
            pm25_unit_img.background = pm_unit

            pm25_point_text.setTextColor(Color.parseColor("#00d6bc"))
        } else if (data > 35 && data < 76) {
            var pm_graph_point:Drawable? = getDrawable(R.drawable.pm_graph_point_bad)
            pm25_graph_point.background = pm_graph_point

            var pm_level_text:Drawable? = getDrawable(R.drawable.bad_text)
            pm25_level_text_img.background = pm_level_text

            var pm_unit:Drawable? = getDrawable(R.drawable.unit_bad_ic)
            pm25_unit_img.background = pm_unit

            pm25_point_text.setTextColor(Color.parseColor("#ffbb00"))
        } else if (data > 75) {
            var pm_graph_point:Drawable? = getDrawable(R.drawable.pm_graph_point_verybad)
            pm25_graph_point.background = pm_graph_point

            var pm_level_text:Drawable? = getDrawable(R.drawable.verybad_text)
            pm25_level_text_img.background = pm_level_text

            var pm_unit:Drawable? = getDrawable(R.drawable.unit_verybad_ic)
            pm25_unit_img.background = pm_unit

            pm25_point_text.setTextColor(Color.parseColor("#f15f86"))
        }

        var d:Int = data
        if (data > 99) {
            d = 99
        }
        val rotation = (d * 3.6).toFloat()
        pm25_graph_point.rotation = rotation

        pm25_point_text.setText(d.toString())
    }

    private fun tempViewUpdate(data: Int) {
        temp_text.setText(data.toString())
    }

    private fun humiViewUpdate(data: Int) {
        hum_text.setText(data.toString())
    }

    private  fun co2ViewUpdate(data: Int) {
        co2_text.setText(data.toString())
    }

    private fun vocViewUpdate(data: Int) {
        val param = voc_level_img.layoutParams as FrameLayout.LayoutParams
        if (data == 0) {
            voc_level_img.background = getDrawable(R.drawable.low)
            param.setMargins(1717, 750, 0,0)
            param.width = 126
            param.height = 65
            voc_level_img.layoutParams = param
        } else if (data == 1) {
            voc_level_img.background = getDrawable(R.drawable.acceptable)
            param.setMargins(1558, 750, 0,0)
            param.width = 285
            param.height = 65
            voc_level_img.layoutParams = param
        }
        else if (data == 2) {
            voc_level_img.background = getDrawable(R.drawable.marginal)
            param.setMargins(1613, 750, 0,0)
            param.width = 230
            param.height = 65
            voc_level_img.layoutParams = param
        }
        else if (data == 3) {
            voc_level_img.background = getDrawable(R.drawable.high)
            param.setMargins(1720, 750, 0,0)
            param.width = 123
            param.height = 65
            voc_level_img.layoutParams = param
        }
    }

    // 2019.09.10 네오카텍 app 에서 보낸 센서 data 값을 브로드캐스트로 받은 후 값이 0보다 크면 remove 동영상 재생
    class DataBroadcastReceiver(activity: MainActivity) : BroadcastReceiver(){
        private val mActivity: MainActivity
        public var isRunningVideo = false
        init {
            mActivity = activity
        }


        override fun onReceive(context: Context, intent: Intent) {
            var action = intent!!.action

            when(action) {
                mActivity.ACTION_DATA -> {
                    val data = intent.getIntExtra("sensor", 0)
                    Log.d("by_debug","receive data $data")
                    if (data > 0) {
                        if(!isRunningVideo){
                            isRunningVideo = true
                            //  mActivity.startVideo(data)
                            mActivity.removeVideo()
                        }
                    }

                    val timeData = intent.getStringExtra("info_date")
                    if (timeData != null) {
                        if (timeData.length > 0) {
                            mActivity.timeChanged(mActivity.getCurrentTime(System.currentTimeMillis()))
                            mActivity.waitVideo()
                        }
                    }

                    val tempData = intent.getIntExtra("info_temp", -1)
                    if (tempData != -1) {
                        //val temp:Int = tempData.toInt()
                        Log.d("by_debug","air quality temp data $tempData")
                        mActivity.tempViewUpdate(tempData)
                    }

                    val humiData = intent.getIntExtra("info_humi", -1)
                    if (humiData != -1) {
                        //val humi:Int = humiData.toInt()
                        Log.d("by_debug","air quality humi data $humiData")
                        mActivity.humiViewUpdate(humiData)
                    }

                    val vocData = intent.getIntExtra("info_voc", -1)
                    if (vocData != -1) {
                        //val voc:Int = vocData.toInt()
                        Log.d("by_debug","air quality voc data $vocData")
                        mActivity.vocViewUpdate(vocData)
                    }

                    val co2Data = intent.getIntExtra("info_co2", -1)
                    if (co2Data != -1) {
                        //val co2:Int = co2Data.toInt()
                        Log.d("by_debug","air quality co2 data $co2Data")
                        mActivity.co2ViewUpdate(co2Data)
                    }

                    val pm25Data = intent.getIntExtra("info_pm25", -1)
                    if (pm25Data != -1) {
                        //val pm25:Int = pm25Data.toInt()
                        Log.d("by_debug","air quality pm25 data $pm25Data")
                        mActivity.pm25ViewUpdate(pm25Data)
                    }

                    val pm10Data = intent.getIntExtra("info_pm10", -1)
                    if (pm10Data != -1) {
                        //val pm10:Int = pm10Data.toInt()
                        Log.d("by_debug","air quality pm10 data $pm10Data")
                        mActivity.pm10ViewUpdate(pm10Data)
                    }
                }
            }
        }
    }

}
