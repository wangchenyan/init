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
) : Comparable<TaskInfo> {
    val depends: Set<String>
    val process: Set<String>
    val children: MutableSet<TaskInfo> = mutableSetOf()

    init {
        this.depends = HashSet(listOf(*depends))
        this.process = HashSet(listOf(*process))
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is TaskInfo && other.name == name
    }

    override fun compareTo(other: TaskInfo): Int {
        return priority.compareTo(other.priority)
    }
}