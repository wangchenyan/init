package me.wcy.arch.module

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import me.wcy.arch.annotation.AbsModule
import kotlin.properties.Delegates

/**
 * Created by wcy on 2020/12/10.
 */
internal object ModuleStarter {
    private val moduleList by lazy {
        ModuleManager.getModuleList()
    }
    private var isMainProcess by Delegates.notNull<Boolean>()

    fun init(context: Context) {
        isMainProcess = ProcessUtils.isMainProcess(context)
        moduleList.sort()
        moduleList.forEach { module ->
            if (shouldInvoke(module)) {
                (module as BaseModule).onCreate(context)
            }
        }

        context.applicationContext.registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onLowMemory() {
                moduleList.forEach { module ->
                    if (shouldInvoke(module)) {
                        module.onLowMemory()
                    }
                }
            }

            override fun onConfigurationChanged(newConfig: Configuration) {
            }

            override fun onTrimMemory(level: Int) {
                moduleList.forEach { module ->
                    if (shouldInvoke(module)) {
                        module.onTrimMemory(level)
                    }
                }
            }
        })
    }

    private fun shouldInvoke(module: AbsModule): Boolean {
        return module is BaseModule && (isMainProcess || module.isSupportMultiProcess)
    }
}