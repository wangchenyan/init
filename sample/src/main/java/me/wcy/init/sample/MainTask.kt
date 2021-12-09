package me.wcy.init.sample

import android.app.Application
import android.os.SystemClock
import android.util.Log
import me.wcy.init.annotation.InitTask
import me.wcy.init.api.IInitTask

/**
 * Created by wcy on 2020/12/10.
 */
@InitTask(
    name = "main1",
    process = [InitTask.PROCESS_MAIN],
    depends = ["lib1"]
)
class MainTask : IInitTask {
    override fun execute(application: Application) {
        SystemClock.sleep(1000)
        Log.e("WCY", "main1 execute")
    }
}

@InitTask(
    name = "main2",
    background = true,
    depends = ["main1", "lib1"]
)
class MainTask2 : IInitTask {
    override fun execute(application: Application) {
        SystemClock.sleep(1000)
        Log.e("WCY", "main2 execute")
    }
}

@InitTask(
    name = "main3",
    priority = InitTask.PRIORITY_HIGH,
)
class MainTask3 : IInitTask {
    override fun execute(application: Application) {
        SystemClock.sleep(1000)
        Log.e("WCY", "main3 execute")
    }
}