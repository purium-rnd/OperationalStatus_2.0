package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // 전달된 값이 '부팅완료' 인 경우에만 동작 하도록 조건문을 설정 해줍니다.
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            Intent startApp = new Intent(context,MainActivity.class);
            startApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startApp);
        }
    }
}
