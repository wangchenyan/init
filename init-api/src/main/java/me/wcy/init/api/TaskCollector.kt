package me.wcy.init.api

import me.wcy.init.annotation.TaskInfo
import me.wcy.init.annotation.TaskLoader

/**
 * Created by wcy on 2020/12/10.
 */
internal class TaskCollector {
    val taskList: MutableList<TaskInfo> = mutableListOf()

    init {
        init()
    }

    private fun init() {}

    fun register(loader: TaskLoader) {
        loader.loadTask(taskList)
    }
}