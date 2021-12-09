package me.wcy.init.api

import android.os.Looper

/**
 * Created by wangchenyan.top on 2021/12/9.
 */
object ThreadUtils {

    fun isInMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}