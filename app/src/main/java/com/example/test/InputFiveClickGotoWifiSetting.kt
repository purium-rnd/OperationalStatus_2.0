package com.example.test

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log

class InputFiveClickGotoWifiSetting {
    private var clickTime:Long  = 0
    private var clickCount:Int = 0

    public fun onClickLogoImg(context: Context) {
        if(clickTime == 0L)
            clickTime = System.currentTimeMillis()

        if (System.currentTimeMillis() > clickTime + 1000) {
            clickCount = 0
            clickTime = 0L
            return
        }

        if (System.currentTimeMillis() <= clickTime + 1000) {
            clickTime = System.currentTimeMillis()
            clickCount++
            if(clickCount == 2){
                val intent = Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
                context.startActivity(intent)
            }
        }
        Log.d("by_debug","click count = $clickCount")
    }
}