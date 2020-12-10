package me.wcy.arch.module

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.text.TextUtils
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by wcy on 2020/12/10.
 */
object ProcessUtils {

    fun isMainProcess(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val packageName = context.applicationContext.packageName
        val processName = getProcessName(context)
        return packageName == processName
    }

    fun getProcessName(context: Context): String? {
        var processName = getProcessFromFile()
        if (processName == null || processName.isEmpty()) {
            // 如果装了xposed一类的框架，上面可能会拿不到，回到遍历迭代的方式
            processName = getProcessNameByAM(context)
        }
        return processName
    }

    private fun getProcessFromFile(): String? {
        var reader: BufferedReader? = null
        return try {
            val pid = Process.myPid()
            val file = "/proc/$pid/cmdline"
            reader = BufferedReader(InputStreamReader(FileInputStream(file), "iso-8859-1"))
            var c: Int
            val processName = StringBuilder()
            while (reader.read().also { c = it } > 0) {
                processName.append(c.toChar())
            }
            processName.toString()
        } catch (e: Exception) {
            null
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getProcessNameByAM(context: Context): String? {
        var processName: String? = null
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        while (true) {
            val plist = am.runningAppProcesses
            if (plist != null) {
                for (info in plist) {
                    if (info.pid == Process.myPid()) {
                        processName = info.processName
                        break
                    }
                }
            }
            if (!TextUtils.isEmpty(processName)) {
                return processName
            }
            try {
                Thread.sleep(100L) // take a rest and again
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }
    }
}