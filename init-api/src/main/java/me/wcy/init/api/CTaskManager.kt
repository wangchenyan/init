package me.wcy.init.api

import android.app.Application
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import me.wcy.init.annotation.InitTask
import me.wcy.init.annotation.TaskInfo
import kotlin.system.measureTimeMillis

/**
 * Created by wangchenyan.top on 2021/12/9.
 */
class CTaskManager private constructor(
    private val app: Application,
    private val processName: String
) {
    private val completedTasks: MutableSet<String> = mutableSetOf()

    fun start() {
        val taskList = TaskCollector().taskList
        checkDuplicateName(taskList)
        taskList.sortBy { it.priority }
        val taskMap = taskList.map { it.name to it }.toMap()
        val singleSyncTasks: MutableSet<TaskInfo> = mutableSetOf()
        val singleAsyncTasks: MutableSet<TaskInfo> = mutableSetOf()

        taskList.forEach { task ->
            when {
                task.depends.isNotEmpty() -> {
                    checkCircularDependency(listOf(task.name), task.depends, taskMap)
                    task.depends.forEach {
                        val depend = taskMap[it]
                        checkNotNull(depend) {
                            "Can not find task [$it] which depend by task [${task.name}]"
                        }
                        depend.children.add(task)
                    }
                }
                task.background -> {
                    singleAsyncTasks.add(task)
                }
                else -> {
                    singleSyncTasks.add(task)
                }
            }
        }

        // 无依赖的异步任务
        singleAsyncTasks.forEach { task ->
            flowOf(task).flowOn(Dispatchers.Default).onEach { execute(it) }
                .launchIn(GlobalScope)
        }

        // 无依赖的同步任务
        if (Looper.myLooper() == Looper.getMainLooper()) {
            singleSyncTasks.forEach { execute(it) }
        } else {
            singleSyncTasks.asFlow()
                .flowOn(Dispatchers.Main)
                .onEach { execute(it) }
                .launchIn(GlobalScope)
        }
    }

    private fun checkDuplicateName(taskList: List<TaskInfo>) {
        val set: MutableSet<String> = mutableSetOf()
        taskList.forEach {
            check(set.contains(it.name).not()) {
                "Found multiple tasks with the same name: [${it.name}]"
            }
            set.add(it.name)
        }
    }

    private fun checkCircularDependency(
        chain: List<String>,
        depends: Set<String>,
        taskMap: Map<String, TaskInfo>
    ) {
        depends.forEach { depend ->
            check(chain.contains(depend).not()) {
                "Found circular dependency chain: $chain -> $depend"
            }
            taskMap[depend]?.let { task ->
                checkCircularDependency(chain + depend, task.depends, taskMap)
            }
        }
    }

    private fun execute(task: TaskInfo) {
        if (isMatchProgress(task)) {
            val cost = measureTimeMillis {
                kotlin.runCatching {
                    (task.task as IInitTask).execute(app)
                }.onFailure {
                    Log.e(TAG, "executing task [${task.name}] error", it)
                }
            }
            Log.d(
                TAG, "Execute task [${task.name}] complete in process [$processName] " +
                        "thread [${Thread.currentThread().name}], cost: ${cost}ms"
            )
        } else {
            Log.w(
                TAG,
                "Skip task [${task.name}] cause the process [$processName] not match"
            )
        }
        afterExecute(task)
    }

    private fun afterExecute(task: TaskInfo) = synchronized(completedTasks) {
        completedTasks.add(task.name)
        task.children.filter { completedTasks.containsAll(it.depends) }
            .forEach { child ->
                val dispatcher = if (child.background) Dispatchers.Default else Dispatchers.Main
                flowOf(child).flowOn(dispatcher)
                    .onEach { execute(it) }
                    .launchIn(GlobalScope)
            }
    }

    private fun isMatchProgress(task: TaskInfo): Boolean {
        val mainProgressName = app.applicationInfo.processName
        task.process.forEach {
            if (it == InitTask.PROCESS_ALL) {
                return true
            } else if (it == InitTask.PROCESS_MAIN) {
                if (processName == mainProgressName) {
                    return true
                }
            } else if (it == InitTask.PROCESS_NOT_MAIN) {
                if (processName != mainProgressName) {
                    return true
                }
            } else if (it.startsWith(":")) {
                if (processName == mainProgressName + it) {
                    return true
                }
            } else {
                if (it == processName) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        private const val TAG = "CTaskManager"

        fun start(app: Application, processName: String) {
            CTaskManager(app, processName).start()
        }
    }
}