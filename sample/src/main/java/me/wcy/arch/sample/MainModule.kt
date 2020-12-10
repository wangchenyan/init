package me.wcy.arch.sample

import android.content.Context
import android.util.Log
import me.wcy.arch.annotation.AbsModule
import me.wcy.arch.annotation.Module

/**
 * Created by wcy on 2020/12/10.
 */
@Module
class MainModule : AbsModule() {
    override fun onCreate(context: Any?) {
        if (context is Context) {
            Log.e("MainModule", "MainModule onCreate")
        }
    }
}