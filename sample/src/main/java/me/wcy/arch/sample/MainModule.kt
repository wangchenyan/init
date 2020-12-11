package me.wcy.arch.sample

import android.content.Context
import android.util.Log
import me.wcy.arch.annotation.Module
import me.wcy.arch.module.BaseModule

/**
 * Created by wcy on 2020/12/10.
 */
@Module
class MainModule : BaseModule() {

    override fun onCreate(context: Context) {
        Log.e("MainModule", "MainModule onCreate")
    }

    override fun isSupportMultiProcess(): Boolean {
        return false
    }
}