package me.wcy.init.api

import me.wcy.init.annotation.ModuleTaskRegister
import me.wcy.init.annotation.TaskInfo

/**
 * 汇总所有模块的 Task
 * Created by wcy on 2020/12/10.
 */
internal class FinalTaskRegister {
    val taskList: MutableList<TaskInfo> = mutableListOf()

    init {
        init()
    }

    private fun init() {}

    fun register(register: ModuleTaskRegister) {
        register.register(taskList)
    }
}