package me.wcy.init.sample_lib

import android.app.Application
import android.os.SystemClock
import android.util.Log
import me.wcy.init.annotation.InitTask
import me.wcy.init.api.IInitTask

/**
 * Created by wcy on 2020/12/10.
 */
@InitTask(
    name = "lib1",
    background = true
)
class LibTask : IInitTask {
    override fun execute(application: Application) {
        SystemClock.sleep(1000)
        Log.e("WCY", "lib1 execute")
    }
}