package me.wcy.init.annotation

import java.util.*

/**
 * Created by wangchenyan.top on 2021/12/9.
 */
class TaskInfo(
    val name: String,
    val background: Boolean,
    val priority: Int,
    process: Array<String>,
    depends: Array<String>,
    val task: ITask
) {
    val depends: Set<String>
    val process: Set<String>
    val children: MutableList<TaskInfo> = mutableListOf()

    init {
        this.depends = HashSet(listOf(*depends))
        this.process = HashSet(listOf(*process))
    }
}