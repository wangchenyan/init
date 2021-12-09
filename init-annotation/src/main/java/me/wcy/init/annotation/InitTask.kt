package me.wcy.init.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class InitTask(
    /**
     * 任务名称，需唯一
     */
    val name: String,
    /**
     * 是否在后台线程执行
     */
    val background: Boolean = false,
    /**
     * 优先级，越小优先级越高
     */
    val priority: Int = PRIORITY_NORM,
    /**
     * 任务执行进程，支持主进程、非主进程、所有进程、:xxx、特定进程名
     */
    val process: Array<String> = [PROCESS_ALL],
    /**
     * 依赖的任务
     */
    val depends: Array<String> = []
) {
    companion object {
        const val PRIORITY_MAX = Int.MIN_VALUE
        const val PRIORITY_HIGH = -1000
        const val PRIORITY_NORM = 0
        const val PRIORITY_LOW = 1000
        const val PRIORITY_MIN = Int.MAX_VALUE

        const val PROCESS_MAIN = "PROCESS_MAIN"
        const val PROCESS_NOT_MAIN = "PROCESS_NOT_MAIN"
        const val PROCESS_ALL = "PROCESS_ALL"
    }
}