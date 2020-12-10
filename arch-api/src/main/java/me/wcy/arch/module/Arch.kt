package me.wcy.arch.module

import android.content.Context

/**
 * Created by wcy on 2020/12/10.
 */
object Arch {

    fun init(context: Context) {
        ModuleStarter.init(context)
    }
}