package me.wcy.arch.module

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import me.wcy.arch.annotation.AbsModule

/**
 * Created by wcy on 2020/12/10.
 */
internal object ModuleStarter {
    private lateinit var moduleList: MutableList<AbsModule>

    fun init(context: Context) {
        moduleList = ModuleManager.getModuleList()
        moduleList.sort()

        moduleList.forEach { module ->
            if (ProcessUtils.isMainProcess(context) || module.isSupportMultiProcess) {
                module.onCreate(context)
            }
        }

        context.applicationContext.registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onLowMemory() {
                moduleList.forEach { module ->
                    if (ProcessUtils.isMainProcess(context) || module.isSupportMultiProcess) {
                        module.onLowMemory()
                    }
                }
            }

            override fun onConfigurationChanged(newConfig: Configuration) {
            }

            override fun onTrimMemory(level: Int) {
                moduleList.forEach { module ->
                    if (ProcessUtils.isMainProcess(context) || module.isSupportMultiProcess) {
                        module.onTrimMemory(level)
                    }
                }
            }
        })
    }
}