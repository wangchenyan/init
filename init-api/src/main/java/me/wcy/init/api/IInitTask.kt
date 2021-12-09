package me.wcy.init.api

import android.app.Application
import me.wcy.init.annotation.ITask

/**
 * Created by wangchenyan.top on 2021/12/8.
 */
interface IInitTask : ITask {
    fun execute(application: Application)
}