package me.wcy.init.sample

import android.app.Application
import me.wcy.init.api.CTaskManager

/**
 * Created by wangchenyan.top on 2021/12/9.
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CTaskManager.start(this)
    }
}