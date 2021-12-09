package me.wcy.init.annotation

/**
 * Created by wangchenyan.top on 2021/12/9.
 */
interface TaskLoader {
    fun loadTask(taskList: List<TaskInfo>)
}