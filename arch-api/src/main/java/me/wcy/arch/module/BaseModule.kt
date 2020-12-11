package me.wcy.arch.module

import android.content.Context
import me.wcy.arch.annotation.AbsModule

/**
 * Created by wcy on 2020/12/11.
 */
abstract class BaseModule : AbsModule() {
    abstract fun onCreate(context: Context)
}
