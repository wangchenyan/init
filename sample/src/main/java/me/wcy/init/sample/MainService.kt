package me.wcy.init.sample

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Created by wangchenyan.top on 2021/12/9.
 */
class MainService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.e("WCY", "service start")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}