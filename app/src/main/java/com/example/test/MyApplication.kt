package com.example.test

import android.app.Application
import org.acra.ACRA
import org.acra.ErrorReporter
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes

@ReportsCrashes(
    formKey = "",
    resToastText = R.string.app_name,
    mode = ReportingInteractionMode.TOAST
)
class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        ACRA.init(this);
        ACRA.getErrorReporter().setReportSender(LocalReportSender(this));
    }
}