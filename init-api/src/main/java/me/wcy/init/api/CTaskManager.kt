package me.wcy.init.api

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.wcy.init.annotation.InitTask
import me.wcy.init.annotation.TaskInfo
import kotlin.system.measureTimeMillis

/**
 * Created by wangchenyan.top on 2021/12/9.
 */
class CTaskManager private constructor(
    private val app: Application,
    private val processName: String,
    private val taskList: List<TaskInfo>,
    private val onTaskComplete: ((String) -> Unit)?,
    private val onAllTaskComplete: (() -> Unit)?
) : CoroutineScope by GlobalScope {
    private val completedTasks: MutableSet<String> = mutableSetOf()

    fun start() {
        checkDuplicateName(taskList)
        val sortedList = taskList.sorted()
        val taskMap = sortedList.associateBy { it.name }
        val singleSyncTasks: MutableSet<TaskInfo> = mutableSetOf()
        val singleAsyncTasks: MutableSet<TaskInfo> = mutableSetOf()

        sortedList.forEach { task ->
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
            launch(Dispatchers.Default) { execute(task) }
        }

        // 无依赖的同步任务
        launch(Dispatchers.Main.immediate) {
            singleSyncTasks.forEach { execute(it) }
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
                onTaskComplete?.invoke(task.name)
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
        afterExecute(task.name, task.children)
    }

    private fun afterExecute(name: String, children: Set<TaskInfo>) {
        val allowTasks = synchronized(completedTasks) {
            completedTasks.add(name)
            children.filter { completedTasks.containsAll(it.depends) }
        }.sorted()
        allowTasks.forEach {
            val dispatcher = if (it.background) Dispatchers.Default else Dispatchers.Main.immediate
            launch(dispatcher) { execute(it) }
        }
        if (taskList.size == completedTasks.size) {
            onAllTaskComplete?.invoke()
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

        /**
         * 启动任务
         *
         * @param processName 当前进程名，如果 [start] 内部获取进程名不准确，可自行传入
         * @param onTaskComplete 单个任务执行完成，在任务所在线程回调
         * @param onAllTaskComplete 所有任务执行完成，在最后一个任务所在线程回调
         */
        @JvmStatic
        @JvmOverloads
        fun start(
            app: Application,
            processName: String = ProcessUtils.getProcessName(app),
            onTaskComplete: ((String) -> Unit)? = null,
            onAllTaskComplete: (() -> Unit)? = null,
        ) {
            val taskList = FinalTaskRegister().taskList
            CTaskManager(app, processName, taskList, onTaskComplete, onAllTaskComplete).start()
        }
    }
}