package me.wcy.arch.sample_lib

import android.content.Context
import android.util.Log
import me.wcy.arch.annotation.BaseModule
import me.wcy.arch.annotation.Module

/**
 * Created by wcy on 2020/12/10.
 */
@Module
class LibModule : BaseModule() {
    override fun onCreate(context: Any?) {
        if (context is Context) {
            Log.e("LibModule", "LibModule onCreate")
        }
    }

    override fun getPriority(): Int {
        return MAX_PRIORITY
    }

    override fun isSupportMultiProcess(): Boolean {
        return true
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.e("LibModule", "LibModule onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.e("LibModule", "LibModule onTrimMemory")
    }
}