package me.wcy.init.annotation

/**
 * 通过 APT 收集模块的 Task
 * Created by wangchenyan.top on 2021/12/9.
 */
interface ModuleTaskRegister {
    fun register(taskList: MutableList<TaskInfo>)
}